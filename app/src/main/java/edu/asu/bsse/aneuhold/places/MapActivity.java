package edu.asu.bsse.aneuhold.places;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
 * Purpose: The primary activity for the map interface. Allows adding of places by long pressing
 * the screen where the new place should be located.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 24, 2019
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener, DialogInterface.OnClickListener {

  private GoogleMap placesMap;
  private ArrayList<String> placeNames;
  private EditText in;
  private LatLng point;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    placeNames = new ArrayList<>(Arrays.asList(PlaceDB.getPlaceNamesFromDB(this)));

    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    //region Toolbar Setup
    Toolbar toolbar = findViewById(R.id.map_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    //endregion
  }

  @Override
  public boolean onSupportNavigateUp() {
    Intent intent = getIntent();
    setResult(RESULT_OK, intent);
    finish();
    return true;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    placesMap = googleMap;
    placesMap.setOnMapLongClickListener(this);
    for (String placeName : placeNames) {
      PlaceDescription place = PlaceDB.getPlaceDescriptionFromDB(placeName, this);
      placesMap.addMarker(new MarkerOptions().position(
          new LatLng(place.getLatitude(), place.getLongitude())).title(placeName));
    }

    // Set the first place as the place to start the camera
    PlaceDescription firstPlace = PlaceDB.getPlaceDescriptionFromDB(placeNames.get(0), this);
    placesMap.animateCamera(CameraUpdateFactory.newCameraPosition(
        new CameraPosition(
            new LatLng(firstPlace.getLatitude(), firstPlace.getLongitude()),
            (float)9.0, (float)0.0, (float)0.0)
    ));

  }

  /**
   * Called when pointer capture is enabled or disabled for the current window.
   *
   * @param hasCapture True if the window has pointer capture.
   */
  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {
    // Left empty
  }

  @Override
  public void onMapLongClick(LatLng latLng) {
    this.point = latLng;
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.map_dialog_text));
    in = new EditText(this);
    in.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(in);
    builder.setNegativeButton(getString(R.string.cancel), this);
    builder.setPositiveButton(getString(R.string.ok),this);
    builder.show();
  }

  /**
   * This method will be invoked when a button in the dialog is clicked.
   *
   * @param dialog the dialog that received the click
   * @param which  the button that was clicked (ex.
   *               {@link DialogInterface#BUTTON_POSITIVE}) or the position
   */
  @Override
  public void onClick(DialogInterface dialog, int which) {
    String result = (which==DialogInterface.BUTTON_POSITIVE)? getString(R.string.ok):
        getString(R.string.cancel);
    android.util.Log.d(this.getClass().getSimpleName(),"onClick result: "+result+
        " input is: "+in.getText());

    // Add the new place to the local placeNames array and to the database
    placeNames.add(in.getText().toString());
    PlaceDescription newPlaceDescription = new PlaceDescription();
    newPlaceDescription.setPlaceName(in.getText().toString());
    newPlaceDescription.setLatitude(point.latitude);
    newPlaceDescription.setLongitude(point.longitude);
    PlaceDB.addPlaceInDB(newPlaceDescription, this);

    // Add the new place to the map
    placesMap.addMarker(new MarkerOptions()
        .position(point)
        .title(in.getText().toString())
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
  }
}
