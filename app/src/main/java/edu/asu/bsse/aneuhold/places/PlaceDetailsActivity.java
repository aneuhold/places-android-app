package edu.asu.bsse.aneuhold.places;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
 * Purpose: Provides the Activity for a specific place's details screen.
 * This information is populated from a remote JSON RPC server.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 10, 2019
 */
public class PlaceDetailsActivity extends AppCompatActivity {

  /**
   * This is used to indicate to the mainActivity activity (MainActivity) that the user pressed the
   * delete option while editing the place description.
   */
  public final static int DELETE_PLACE_DESCRIPTION = 2;
  boolean isNewPlaceDescription = false;
  public PlaceDescription placeDescription;
  public String placeName;

  //TODO: This class will need to be changed so that a user cannot create a new place description
  // then submit it without a unique name.

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_place_details);

    // Get the intent and set the PlaceDescription object
    Intent intent = getIntent();
    if (intent.getIntExtra("requestCode", -1)
        == MainActivity.ADD_PLACE_DESCRIPTION_REQUEST) {
      isNewPlaceDescription = true;
    }

    /* Request the place description object from AsyncPlacesConnect using the passed in place name.
     * The hydrateTextFields method will be called once this is complete.
    */
    if (!isNewPlaceDescription) {
      placeName = intent.getStringExtra(MainActivity.PLACE_NAME);
      RPCMethodInformation mi = new RPCMethodInformation(null,
          this.getResources().getString(R.string.default_url_string),
          "get", new String[]{placeName});
      mi.callingActivity = this;
      new AsyncPlacesConnect().execute(mi);
    }

    this.placeDescription = (PlaceDescription) intent.getSerializableExtra(MainActivity.PLACE_DESCRIPTION);

    // Hydrate the fields if the placeDescription is valid
    if (placeDescription != null) {
      System.out.println("The passed placeDescription is: " + placeDescription.toString());
      hydrateTextFields();
    } else {
      System.out.println("The passed placeDescription was null. Creating new PlaceDescription.");
      placeDescription = new PlaceDescription();
    }

    //region Toolbar Setup
    Toolbar toolbar = findViewById(R.id.mainToolbar);
    setSupportActionBar(toolbar);
    //endregion
  }

  //region Menu Methods
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_place_details, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.menu_action_delete) {
      Intent intent = getIntent();
      intent.putExtra(MainActivity.PLACE_DESCRIPTION, placeDescription);
      setResult(DELETE_PLACE_DESCRIPTION, intent);
      finish();
    }
    return true;
  }
  //endregion

  public void onClickDone(View v) {
    collectEditedFields();
    Intent intent = getIntent();
    intent.putExtra(MainActivity.PLACE_DESCRIPTION, placeDescription);
    setResult(RESULT_OK, intent);
    finish();
  }

  /**
   * The user isn't allowed to change the name of the place if it isn't a new place description.
   */
  private void collectEditedFields() {
    if (isNewPlaceDescription) {
      EditText placeNameTextBox = findViewById(R.id.placeNameTextBox);
      placeDescription.setPlaceName(placeNameTextBox.getText().toString());
    }

    EditText placeDescriptionTextBox = findViewById(R.id.placeDescriptionTextBox);
    EditText placeAddressTitleTextBox = findViewById(R.id.placeAddressTitleTextBox);
    EditText placeCategoryTextBox = findViewById(R.id.placeCategoryTextBox);
    EditText placeAddressStreetTextBox = findViewById(R.id.placeAddressStreetTextBox);
    EditText placeElevationTextBox = findViewById(R.id.placeElevationTextBox);
    EditText placeLatitudeTextBox = findViewById(R.id.placeLatitudeTextBox);
    EditText placeLongitudeTextBox = findViewById(R.id.placeLongitudeTextBox);

    placeDescription.setPlaceDescription(placeDescriptionTextBox.getText().toString());
    placeDescription.setAddressTitle(placeAddressTitleTextBox.getText().toString());
    placeDescription.setCategory(placeCategoryTextBox.getText().toString());
    placeDescription.setAddressStreet(placeAddressStreetTextBox.getText().toString());

    // Determine if the number entry boxes are empty, if they are, then set the value to 0.
    if (placeElevationTextBox.getText().toString().isEmpty()) {
      placeDescription.setElevation(0);
    } else {
      placeDescription.setElevation(Double.parseDouble(placeElevationTextBox.getText().toString()));
    }
    if (placeLatitudeTextBox.getText().toString().isEmpty()) {
      placeDescription.setLatitude(0);
    } else {
      placeDescription.setLatitude(Double.parseDouble(placeLatitudeTextBox.getText().toString()));
    }
    if (placeLongitudeTextBox.getText().toString().isEmpty()) {
      placeDescription.setLongitude(0);
    } else {
      placeDescription.setLongitude(Double.parseDouble(placeLongitudeTextBox.getText().toString()));
    }

  }

  /**
   * Hydrates the text fields for this class using the placeDescription object
   */
  public void hydrateTextFields() {

    // Collect all the TextBox variables!
    EditText placeNameTextBox = findViewById(R.id.placeNameTextBox);
    EditText placeDescriptionTextBox = findViewById(R.id.placeDescriptionTextBox);
    EditText placeAddressTitleTextBox = findViewById(R.id.placeAddressTitleTextBox);
    EditText placeCategoryTextBox = findViewById(R.id.placeCategoryTextBox);
    EditText placeAddressStreetTextBox = findViewById(R.id.placeAddressStreetTextBox);
    EditText placeElevationTextBox = findViewById(R.id.placeElevationTextBox);
    EditText placeLatitudeTextBox = findViewById(R.id.placeLatitudeTextBox);
    EditText placeLongitudeTextBox = findViewById(R.id.placeLongitudeTextBox);

    // Hydrate all the TextBox text values!
    placeNameTextBox.setText(placeDescription.getPlaceName());
    placeDescriptionTextBox.setText(placeDescription.getPlaceDescription());
    placeAddressTitleTextBox.setText(placeDescription.getAddressTitle());
    placeCategoryTextBox.setText(placeDescription.getCategory());
    placeAddressStreetTextBox.setText(placeDescription.getAddressStreet());
    placeElevationTextBox.setText(String.format("%s", placeDescription.getElevation()));
    placeLatitudeTextBox.setText(String.format("%s", placeDescription.getLatitude()));
    placeLongitudeTextBox.setText(String.format("%s", placeDescription.getLongitude()));
  }
}
