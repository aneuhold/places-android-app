package edu.asu.bsse.aneuhold.places;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class DistanceCalcActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
  private static final double EARTH_AVERAGE_RADIUS_MILES = 3958.8;
  public String[] placeNames;
  public ArrayAdapter<String> startArrayAdapter;
  public ArrayAdapter<String> endArrayAdapter;
  public PlaceDescription startPlace;
  public PlaceDescription endPlace;
  public boolean waitingOnStartPlace = false;
  public boolean waitingOnEndPlace = false;
  private Spinner startSpinner;
  private Spinner endSpinner;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_distance_calculator);

    // Retrieve the list of place names.
    RPCMethodInformation mi = new RPCMethodInformation(null,
        getResources().getString(R.string.default_url_string),
        "getNames",
        new Object[]{});
    mi.callingActivity = this;
    new AsyncPlacesConnect().execute(mi);

    //region Toolbar Setup
    Toolbar toolbar = findViewById(R.id.distanceCalcToolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    //endregion
  }

  /**
   * To be called after placeNames has been set from the AsyncPlacesConnect class
   */
  public void initializeSpinners() {

    // Assign the spinners
    startSpinner = findViewById(R.id.distSpinnerStartingLoc);
    endSpinner = findViewById(R.id.distSpinnerEndingLoc);

    // Create the ArrayAdapters
    startArrayAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_dropdown_item, placeNames);
    endArrayAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_dropdown_item, placeNames);

    // Assign the ArrayAdapters and onItemSelected listeners
    startSpinner.setAdapter(startArrayAdapter);
    startSpinner.setOnItemSelectedListener(this);
    endSpinner.setAdapter(endArrayAdapter);
    endSpinner.setOnItemSelectedListener(this);
  }

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  /**
   * <p>Callback method to be invoked when an item in this view has been
   * selected. This callback is invoked only when the newly selected
   * position is different from the previously selected position or if
   * there was no selected item.</p>
   * <p>
   * Implementers can call getItemAtPosition(position) if they need to access the
   * data associated with the selected item.
   *
   * @param parent   The AdapterView where the selection happened
   * @param view     The view within the AdapterView that was clicked
   * @param position The position of the view in the adapter
   * @param id       The row id of the item that is selected
   */
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (parent.getId() == R.id.distSpinnerStartingLoc) {
      waitingOnStartPlace = true;
      RPCMethodInformation mi = new RPCMethodInformation(null,
          getResources().getString(R.string.default_url_string),
          "get",
          new String[]{startSpinner.getSelectedItem().toString()});
      mi.callingActivity = this;
      new AsyncPlacesConnect().execute(mi);
    } else if (parent.getId() == R.id.distSpinnerEndingLoc) {
      waitingOnEndPlace = true;
      RPCMethodInformation mi = new RPCMethodInformation(null,
          getResources().getString(R.string.default_url_string),
          "get",
          new String[]{endSpinner.getSelectedItem().toString()});
      mi.callingActivity = this;
      new AsyncPlacesConnect().execute(mi);
    }
  }

  /**
   * This method is called from the AsyncPlacesConnect class.
   */
  public void calculate() {

    // Calculate the new distance
    double newDistance = calculateDistance(startPlace.getLatitude(), startPlace.getLongitude(),
        endPlace.getLatitude(), endPlace.getLongitude());
    TextView resultDistanceTextView = findViewById(R.id.distResultDistanceTextView);
    resultDistanceTextView.setText(String.format(Locale.US, "%f mi", newDistance));

    // Calculate the initial bearing
    double newBearing = calculateBearingInDegrees(startPlace.getLatitude(),
        startPlace.getLongitude(), endPlace.getLatitude(), endPlace.getLongitude());
    TextView resultBearingTextView = findViewById(R.id.distResultBearingTextView);
    resultBearingTextView.setText(String.format(Locale.US, "%f °", newBearing));
  }

  private double calculateDistance(double lat1Dec, double lon1Dec, double lat2Dec, double lon2Dec) {

    // Convert input to radians
    double lat1Rad = Math.toRadians(lat1Dec);
    double lon1Rad = Math.toRadians(lon1Dec);
    double lat2Rad = Math.toRadians(lat2Dec);
    double lon2Rad = Math.toRadians(lon2Dec);

    double r = EARTH_AVERAGE_RADIUS_MILES;

    // Haversine formula - https://en.wikipedia.org/wiki/Haversine_formula
    double result = 2 * r * Math.asin(Math.sqrt(
        Math.pow(Math.sin((lat2Rad - lat1Rad) / 2), 2)
            + Math.cos(lat1Rad)
            * Math.cos(lat2Rad)
            * Math.pow(Math.sin((lon2Rad - lon1Rad) / 2), 2)
    ));

    return result;
  }

  private double calculateBearingInDegrees(double lat1Dec, double lon1Dec, double lat2Dec,
                                           double lon2Dec) {
    // Convert input to radians
    double lat1Rad = Math.toRadians(lat1Dec);
    double lon1Rad = Math.toRadians(lon1Dec);
    double lat2Rad = Math.toRadians(lat2Dec);
    double lon2Rad = Math.toRadians(lon2Dec);

    // Formula retrieved from http://www.movable-type.co.uk/scripts/latlong.html
    double result = Math.atan2(Math.sin(lon2Rad - lon1Rad) * Math.cos(lat2Rad),
        Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) *
        Math.cos(lon2Rad - lon1Rad));

    // Convert to degrees. It starts out somewhere between -180 and +180
    result = (Math.toDegrees(result) + 360) % 360;
    return result;
  }

  /**
   * Callback method to be invoked when the selection disappears from this
   * view. The selection can disappear for instance when touch is activated
   * or when the adapter becomes empty.
   *
   * @param parent The AdapterView that now contains no selected item.
   */
  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Not doing anything for this.
  }


}
