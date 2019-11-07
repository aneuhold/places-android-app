package edu.asu.bsse.aneuhold.places;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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
 * Purpose: Contains the data for a place object. This class can also build a place description
 * from a JSON string provided to the constructor.
 *
 * An example JSON object which can be fed to the constructor of this class is below: <br>
 * <code>
 * {
 *     "name" : "ASU-Poly",
 *     "description" : "Home of ASU's Software Engineering Programs",
 *     "category" : "School",
 *     "address-title" : "ASU Software Engineering",
 *     "address-street" : "7171 E Sonoran Arroyo Mall\nPeralta Hall 230\nMesa AZ 85212",
 *     "elevation" : 1384.0,
 *     "latitude" : 33.306388,
 *     "longitude" : -111.679121
 * }
 * </code>
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version October 20, 2019
 */
public class PlaceDescription implements Serializable {
  private String placeName;
  private String placeDescription;
  private String category;
  private String addressTitle;
  private String addressStreet;
  private double elevation;
  private double longitude;
  private double latitude;

  PlaceDescription() {
    this.placeName = "";
    this.placeDescription = "";
    this.category = "";
    this.addressTitle = "";
    this.addressStreet = "";
  }

  PlaceDescription(JSONObject jo) {
    try {
      this.latitude = jo.getDouble("latitude");
      this.placeName = jo.getString("name");
      this.placeDescription = jo.getString("description");
      this.category = jo.getString("category");
      this.addressTitle = jo.getString("address-title");
      this.addressStreet = jo.getString("address-street");
      this.elevation = jo.getDouble("elevation");
      this.longitude = jo.getDouble("longitude");
    } catch (JSONException e) {
      android.util.Log.w(this.getClass().getSimpleName(),
          "error converting from json");
    }
  }

  PlaceDescription(String jsonStr){
    try {
      JSONObject jo = new JSONObject(jsonStr);
      this.placeName = jo.getString("name");
      this.placeDescription = jo.getString("description");
      this.category = jo.getString("category");
      this.addressTitle = jo.getString("address-title");
      this.addressStreet = jo.getString("address-street");
      this.elevation = jo.getDouble("elevation");
      this.latitude = jo.getDouble("latitude");
      this.longitude = jo.getDouble("longitude");

    } catch (Exception ex){
      android.util.Log.w(this.getClass().getSimpleName(),
          "error converting from json");
    }
  }

  public JSONObject toJsonObj() {
    JSONObject jo = new JSONObject();
    try{
      jo.put("name", placeName);
      jo.put("description", placeDescription);
      jo.put("category", category);
      jo.put("address-title", addressTitle);
      jo.put("address-street", addressStreet);
      jo.put("elevation", elevation);
      jo.put("latitude", latitude);
      jo.put("longitude", longitude);
    }catch (Exception ex){
      android.util.Log.w(this.getClass().getSimpleName(),
          "error converting to json");
    }
    return jo;
  }

  public String toJsonString(){
    String ret = "";
    JSONObject jObj = toJsonObj();
    if (jObj != null) ret = jObj.toString();
    return ret;
  }

  public String getPlaceName() {
    return placeName;
  }

  public void setPlaceName(String placeName) {
    this.placeName = placeName;
  }

  public String getPlaceDescription() {
    return placeDescription;
  }

  public void setPlaceDescription(String placeDescription) {
    this.placeDescription = placeDescription;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getAddressTitle() {
    return addressTitle;
  }

  public void setAddressTitle(String addressTitle) {
    this.addressTitle = addressTitle;
  }

  public String getAddressStreet() {
    return addressStreet;
  }

  public void setAddressStreet(String addressStreet) {
    this.addressStreet = addressStreet;
  }

  public double getElevation() {
    return elevation;
  }

  public void setElevation(double elevation) {
    this.elevation = elevation;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  @Override
  public String toString() {
    return "PlaceDescription{" +
        "placeName='" + placeName + '\'' +
        ", placeDescription='" + placeDescription + '\'' +
        ", category='" + category + '\'' +
        ", addressTitle='" + addressTitle + '\'' +
        ", addressStreet='" + addressStreet + '\'' +
        ", elevation=" + elevation +
        ", longitude=" + longitude +
        ", latitude=" + latitude +
        '}';
  }
}
