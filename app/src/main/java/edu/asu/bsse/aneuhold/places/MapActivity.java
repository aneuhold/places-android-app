package edu.asu.bsse.aneuhold.places;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
 * Purpose: CHANGE ME
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 22, 2019
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener, DialogInterface.OnClickListener {

  private GoogleMap placesMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {

  }

  /**
   * Called when pointer capture is enabled or disabled for the current window.
   *
   * @param hasCapture True if the window has pointer capture.
   */
  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {

  }

  @Override
  public void onMapLongClick(LatLng latLng) {

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

  }
}
