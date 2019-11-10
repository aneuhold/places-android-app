package edu.asu.bsse.aneuhold.places;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Copyright 2019 Anton G Neuhold Jr,
 *
 * This software is the intellectual property of the author, and can not be
 * distributed, used, copied, or reproduced, in whole or in part, for any
 * purpose, commercial or otherwise. The author grants the ASU Software
 * Engineering program the right to copy, execute, and evaluate this work for
 * the purpose of determining performance of the author in coursework, and for
 * Software Engineering program evaluation, so long as this copyright and
 * right-to-use statement is kept in-tact in such use. All other uses are
 * prohibited and reserved to the author.<br>
 * <br>
 *
 * Purpose: The primary activity for the app. This app allows a user to query different saved places
 * on a remote JSONRPC server. It allows the user to edit, add, and delete those places as well as
 * find the distance and bearing between any two locations.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 10, 2019
 */
public class MainActivity extends AppCompatActivity {
  public final static String PLACE_DESCRIPTION = "com.tonyneuhold.PlacesAndroidApp.PLACE_DESCRIPTION";
  public final static String PLACE_NAME = "com.tonyneuhold.PlacesAndroidApp.PLACE_NAME";
  public final static String PLACE_NAMES = "com.tonyneuhold.PlacesAndroidApp.PLACE_NAMES";
  public final static int UPDATE_PLACE_DESCRIPTION_REQUEST = 0;
  public final static int ADD_PLACE_DESCRIPTION_REQUEST = 1;
  private RecyclerView recyclerView;
  public RecyclerViewAdapaterForPlaces recyclerViewAdapter;
  private RecyclerView.LayoutManager recyclerViewLayoutManager;
  public String[] placeNames;
  public PlaceDescription updatedPlaceDescription;

  /**
   * Used for the situation where a place description is being updated. The place is deleted first,
   * then the place is added back in the addPlaceAfterDeletion method.
   */
  public boolean waitingForDelete = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Create link to the toolbar
    Toolbar toolbar = findViewById(R.id.mainToolbar);
    setSupportActionBar(toolbar);

    //region RecyclerView Setup
    // Create link to the RecyclerView
    recyclerView = findViewById(R.id.recycler_view);

    // use this setting to improve performance if you know that changes
    // in content do not change the layout size of the RecyclerView
    recyclerView.setHasFixedSize(false);

    // Set the layout manager
    recyclerViewLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(recyclerViewLayoutManager);

    /*
     * Temporarily setup the new recyclerViewAdapter with no data while the async request is made.
     * Once the async request is finished, it will take care of updating placeNames in the
     * recyclerViewAdapter.
     */
    recyclerViewAdapter = new RecyclerViewAdapaterForPlaces();
    recyclerView.setAdapter(recyclerViewAdapter);

    // Retrieve the list of place names.
    RPCMethodInformation mi = new RPCMethodInformation(this,
        getResources().getString(R.string.default_url_string),
        "getNames",
        new Object[]{});
    new AsyncPlacesConnect().execute(mi);
    //endregion
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.menu_action_add) {

      // Start the PlaceDetailsActivity for a new place
      Intent intent = new Intent(MainActivity.this, PlaceDetailsActivity.class);
      intent.putExtra("requestCode", ADD_PLACE_DESCRIPTION_REQUEST);
      startActivityForResult(intent, ADD_PLACE_DESCRIPTION_REQUEST);
    } else if (item.getItemId() == R.id.menu_action_calculate_distance) {

      // Start the Distance Calculator
      Intent intent = new Intent(MainActivity.this, DistanceCalcActivity.class);
      intent.putExtra(PLACE_NAMES, placeNames);
      startActivity(intent);
    }
    return true;
  }

  /**
   * Opens an instance of PlaceDetailsActivity using the text of the clicked TextView as the
   * argument to provide data to the new activity.
   * @param v
   */
  public void openPlaceDetailsActivity(View v) {
    Intent intent = new Intent(MainActivity.this, PlaceDetailsActivity.class);

    // Get the place description from the view
    CardView cardView = (CardView) v;
    TextView textView = cardView.findViewById(R.id.textView);
    String placeName = textView.getText().toString();

    /*
     * Add the place name, and start the intent. The returned intent is expected to have the updated
     * PlaceDescription object which is handled by onActivityResult.
     */
    intent.putExtra(PLACE_NAME, placeName);
    startActivityForResult(intent, UPDATE_PLACE_DESCRIPTION_REQUEST);

  }

  /**
   * Retrieves the result from a completed activity.
   * @param requestCode
   * @param resultCode
   * @param data
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == UPDATE_PLACE_DESCRIPTION_REQUEST) {
      if (resultCode == RESULT_OK) {

        // If everything came back okay. The user may have made edits
        PlaceDescription placeDescription = (PlaceDescription) data.getSerializableExtra(PLACE_DESCRIPTION);
        System.out.println("The returned placeDescription is: " + placeDescription.toString());
        updatedPlaceDescription = placeDescription;

        // Mark the MainActivity as waiting for deletion
        this.waitingForDelete = true;

        // Initiate the delete, which will then trigger the addition after it is completed.
        RPCMethodInformation mi = new RPCMethodInformation(
            this,
            getResources().getString(R.string.default_url_string),
            "remove",
            new String[]{placeDescription.getPlaceName()});
        new AsyncPlacesConnect().execute(mi);

      } else if (resultCode == PlaceDetailsActivity.DELETE_PLACE_DESCRIPTION) {
        // If the user decided to delete the entry
        PlaceDescription placeDescription = (PlaceDescription) data.getSerializableExtra(PLACE_DESCRIPTION);
        String placeName = placeDescription.getPlaceName();
        RPCMethodInformation mi = new RPCMethodInformation(
            this,
            getResources().getString(R.string.default_url_string),
            "remove",
            new String[]{placeName});
        new AsyncPlacesConnect().execute(mi);
      }
    } else if (requestCode == ADD_PLACE_DESCRIPTION_REQUEST) {
      if (resultCode == RESULT_OK) {
        PlaceDescription placeDescription = (PlaceDescription) data.getSerializableExtra(PLACE_DESCRIPTION);

        // Initiate the addition.
        RPCMethodInformation mi = new RPCMethodInformation(
            this,
            getResources().getString(R.string.default_url_string),
            "add",
            new Object[]{placeDescription.toJsonObj()});
        new AsyncPlacesConnect().execute(mi);
      }
    }
  }

  public void addPlaceAfterDeletion() {
    if (waitingForDelete) {
      waitingForDelete = false;

      // Initiate the addition.
      RPCMethodInformation mi = new RPCMethodInformation(
          this,
          getResources().getString(R.string.default_url_string),
          "add",
          new Object[]{updatedPlaceDescription.toJsonObj()});
      new AsyncPlacesConnect().execute(mi);
    }
  }

}
