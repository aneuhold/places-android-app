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

import java.util.ArrayList;
import java.util.Arrays;

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
 * on a local SQLite3 database. It allows the user to edit, add, and delete those places as well as
 * find the distance and bearing between any two locations. This activity also gives the option to
 * open a map that shows all the places on the map.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 24, 2019
 */
public class MainActivity extends AppCompatActivity {
  public final static String PLACE_DESCRIPTION = "com.tonyneuhold.PlacesAndroidApp.PLACE_DESCRIPTION";
  public final static String PLACE_NAME = "com.tonyneuhold.PlacesAndroidApp.PLACE_NAME";
  public final static String PLACE_NAMES = "com.tonyneuhold.PlacesAndroidApp.PLACE_NAMES";
  public final static int UPDATE_PLACE_DESCRIPTION_REQUEST = 0;
  public final static int ADD_PLACE_DESCRIPTION_REQUEST = 1;
  public final static int OPEN_MAP = 2;
  private RecyclerView recyclerView;
  public RecyclerViewAdapaterForPlaces recyclerViewAdapter;
  private RecyclerView.LayoutManager recyclerViewLayoutManager;
  public ArrayList<String> placeNames;

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

    /*
     Use this setting to improve performance if you know that changes
     in content do not change the layout size of the RecyclerView
    */
    recyclerView.setHasFixedSize(false);

    // Set the layout manager
    recyclerViewLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(recyclerViewLayoutManager);

    // Retrieve the list of place names and setup recyclerViewAdapter
    placeNames = new ArrayList<>(Arrays.asList(PlaceDB.getPlaceNamesFromDB(this)));
    recyclerViewAdapter = new RecyclerViewAdapaterForPlaces(placeNames);
    recyclerView.setAdapter(recyclerViewAdapter);

    //endregion

    // Trigger a sync on startup with the remote JSON RPC server
    syncDBWithJsonServer();
  }

  //region Option Menu methods

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
    } else if (item.getItemId() == R.id.menu_action_map) {

      // Start the map activity
      Intent intent = new Intent(MainActivity.this, MapActivity.class);
      intent.putExtra("requestCode", OPEN_MAP);
      startActivityForResult(intent, OPEN_MAP);

    } else if (item.getItemId() == R.id.menu_action_calculate_distance) {

      // Start the Distance Calculator
      Intent intent = new Intent(MainActivity.this, DistanceCalcActivity.class);
      intent.putExtra(PLACE_NAMES, placeNames);
      startActivity(intent);
    } else if (item.getItemId() == R.id.menu_action_sync_json_server_to_local_db) {
      syncJsonServerToLocalDB();
    } else if (item.getItemId() == R.id.menu_action_sync_local_db_to_json_server) {
      syncLocalDBToJsonServer();
    }
    return true;
  }

  //endregion

  /**
   * Opens an instance of PlaceDetailsActivity using the text of the clicked TextView as the
   * argument to provide data to the new activity.
   * @param v
   */
  public void openPlaceDetailsActivity(View v) {
    Intent intent = new Intent(MainActivity.this, PlaceDetailsActivity.class);

    // Get the place description name from the view
    CardView cardView = (CardView) v;
    TextView textView = cardView.findViewById(R.id.textView);
    String placeName = textView.getText().toString();

    /*
     * Add the place name, DB, then start the intent. The returned intent is expected to have the updated
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
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case UPDATE_PLACE_DESCRIPTION_REQUEST:
        if (resultCode == RESULT_OK) {

          // If everything came back okay. The user may have made edits
          PlaceDescription placeDescription = (PlaceDescription) data.getSerializableExtra(PLACE_DESCRIPTION);
          System.out.println("The returned placeDescription is: " + placeDescription.toString());
          PlaceDB.updatePlaceDescriptionInDB(placeDescription, this);

        } else if (resultCode == PlaceDetailsActivity.DELETE_PLACE_DESCRIPTION) {

          // If the user decided to delete the entry
          PlaceDescription placeDescription = (PlaceDescription) data.getSerializableExtra(PLACE_DESCRIPTION);
          String placeName = placeDescription.getPlaceName();
          PlaceDB.deletePlaceFromDB(placeName, this);
          String[] newPlaceNames = PlaceDB.getPlaceNamesFromDB(this);
          placeNames = new ArrayList<>(Arrays.asList(newPlaceNames));
          recyclerViewAdapter.placeNames = placeNames;
          recyclerViewAdapter.notifyDataSetChanged();
        }
        break;
      case ADD_PLACE_DESCRIPTION_REQUEST:
        if (resultCode == RESULT_OK) {
          PlaceDescription placeDescription = (PlaceDescription) data.getSerializableExtra(PLACE_DESCRIPTION);
          PlaceDB.addPlaceInDB(placeDescription, this);
          String[] newPlaceNames = PlaceDB.getPlaceNamesFromDB(this);

          placeNames = new ArrayList<>(Arrays.asList(newPlaceNames));
          recyclerViewAdapter.placeNames = placeNames;
          recyclerViewAdapter.notifyDataSetChanged();
        }
        break;
      case OPEN_MAP:

        System.out.println("The map returned");
        String[] newPlaceNames = PlaceDB.getPlaceNamesFromDB(this);
        placeNames = new ArrayList<>(Arrays.asList(newPlaceNames));
        recyclerViewAdapter.placeNames = placeNames;
        recyclerViewAdapter.notifyDataSetChanged();
        break;
    }
  }

  /**
   * Syncs the local DB with the JSON server only for items that are new to either the JSON server
   * or the local DB. So if properties have changed for a single place and both the local DB and
   * JSON server have that name stored, then it will not be synced. This is a good method to use
   * to get any quick new places to the server and to the local DB.
   */
  public void syncDBWithJsonServer() {

    // Trigger the method call to get the placeNames from the JSON server.
    RPCMethodInformation mi = new RPCMethodInformation(this,
        getResources().getString(R.string.server_url_string),
        "getNames",
        new Object[]{});
    mi.callbackMethodName = "syncDBWithJsonServerCallback";
    new AsyncPlacesConnect().execute(mi);

  }

  public void syncLocalDBToJsonServer() {

    // Trigger the method call to get the placeNames from the JSON server.
    RPCMethodInformation mi = new RPCMethodInformation(this,
        getResources().getString(R.string.server_url_string),
        "getNames",
        new Object[]{});
    mi.callbackMethodName = "syncLocalDBToJsonServerCallback";
    new AsyncPlacesConnect().execute(mi);

  }

  public void syncJsonServerToLocalDB() {
    PlaceDB.deleteAllFromDB(this);
    placeNames = new ArrayList<>();
    recyclerViewAdapter.placeNames = placeNames;
    recyclerViewAdapter.notifyDataSetChanged();

    // Trigger the method call to get the placeNames from the JSON server.
    RPCMethodInformation mi = new RPCMethodInformation(this,
        getResources().getString(R.string.server_url_string),
        "getNames",
        new Object[]{});
    mi.callbackMethodName = "syncJSonServerToLocalDBCallback";
    new AsyncPlacesConnect().execute(mi);

  }

  public void syncJSonServerToLocalDBCallback(String[] jsonServerPlaceNames) {
    for (String jsonServerPlaceName: jsonServerPlaceNames) {
      RPCMethodInformation mi = new RPCMethodInformation(this,
          getResources().getString(R.string.server_url_string),
          "get",
          new String[]{jsonServerPlaceName});
      mi.callbackMethodName = "PlaceDB.addPlaceInDB";
      new AsyncPlacesConnect().execute(mi);
    }
  }

  public void syncLocalDBToJsonServerCallback(String[] jsonServerPlaceNames) {
    ArrayList<String> serverPlaceNamesArrayList = new ArrayList<>(Arrays.asList(jsonServerPlaceNames));
    for (String placeName: placeNames) {
      if (serverPlaceNamesArrayList.contains(placeName)) {

        // Delete the place on the JSON server, then add it back with the local properties
        RPCMethodInformation mi = new RPCMethodInformation(this,
            getResources().getString(R.string.server_url_string),
            "remove",
            new String[]{placeName});
        mi.callbackMethodName = "add";
        mi.extra.put("addBack", placeName);
        new AsyncPlacesConnect().execute(mi);

        // Delete the place name from the server place names
        serverPlaceNamesArrayList.remove(placeName);

      } else {

        // For those place names that aren't in the server places, simply add them in
        PlaceDescription placeDescription = PlaceDB.getPlaceDescriptionFromDB(placeName, this);
        RPCMethodInformation mi = new RPCMethodInformation(this,
            getResources().getString(R.string.server_url_string),
            "add",
            new Object[]{placeDescription.toJsonObj()});
        new AsyncPlacesConnect().execute(mi);
      }
    }

    // Delete the remaining places that weren't local from the server
    for (String placeName: serverPlaceNamesArrayList) {

      // Delete the place on the JSON server, then add it back with the local properties
      RPCMethodInformation mi = new RPCMethodInformation(this,
          getResources().getString(R.string.server_url_string),
          "remove",
          new String[]{placeName});
      new AsyncPlacesConnect().execute(mi);
    }

    System.out.println("Finished syncing local DB to the JSON server");
  }

  public void syncDBWithJsonServerCallback(String[] jsonServerPlaceNames) {

    /*
     * Create a temporary place names array so that it can be subtracted from while finding matches
     * from the remote server.
     */
    ArrayList<String> tempPlaceNames = new ArrayList<>(placeNames);

    // Loop through jsonServerPlaceNames to see if any do not exist locally. If they don't, add them
    for (String jsonServerPlaceName: jsonServerPlaceNames) {
      if (tempPlaceNames.contains(jsonServerPlaceName)) {
        tempPlaceNames.remove(jsonServerPlaceName);
        System.out.println("tempPlaceNames contains " + jsonServerPlaceName);
      } else {
        System.out.println("tempPlaceNames does not contain " + jsonServerPlaceName +
            "So it is being added from the JSON server");
        RPCMethodInformation mi = new RPCMethodInformation(this,
            getResources().getString(R.string.server_url_string),
            "get",
            new String[]{jsonServerPlaceName});
        mi.callbackMethodName = "PlaceDB.addPlaceInDB";
        new AsyncPlacesConnect().execute(mi);
      }
    }

    // Loop through what is left of the tempPlaceNames array to add to the JSON server
    for (String placeName: tempPlaceNames) {
      PlaceDescription placeDescription = PlaceDB.getPlaceDescriptionFromDB(placeName, this);
      RPCMethodInformation mi = new RPCMethodInformation(this,
          getResources().getString(R.string.server_url_string),
          "add",
          new Object[]{placeDescription.toJsonObj()});
      new AsyncPlacesConnect().execute(mi);
    }
  }

}
