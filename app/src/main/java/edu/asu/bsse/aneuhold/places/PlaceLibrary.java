package edu.asu.bsse.aneuhold.places;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

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
 * Purpose: Contains a library of PLaceDescription objects. This class is currently not used because
 * data is housed on the JSON RPC server.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 10, 2019
 */
public class PlaceLibrary implements Serializable {

  private ArrayList<PlaceDescription> placeDescriptions;

  public PlaceLibrary(String jsonString) {
    placeDescriptions = new ArrayList<>();
    try {

      // Create a new JSONObject by parsing the JSON file
      JSONObject obj = new JSONObject(new JSONTokener(jsonString));

      importJsonObject(obj);
    } catch (JSONException e) {
      System.out.println("The json string could not be parsed");
      e.printStackTrace();
    }
  }

  public boolean modifyPlaceWithName(String name,
                                     PlaceDescription modifiedPlaceDescription) {
    for (int i = 0; i < placeDescriptions.size(); i++) {
      if (placeDescriptions.get(i).getPlaceName().equals(name)) {
        placeDescriptions.set(i, modifiedPlaceDescription);
        System.out.println("The modified place description is: " +
            modifiedPlaceDescription.toString());
        System.out.println("The place description at the modified location is: " +
            placeDescriptions.get(i).toString());
        return true;
      }
    }
    return false;
  }

  public PlaceDescription getPlaceAt (int index) {
    return placeDescriptions.get(index);
  }

  /**
   * Super inefficient search for the PlaceDescription object with the provided name
   * @param name the String containing the desired name of the PlaceDescription object
   * @return the PlaceDescription object with the provided name. Returns null if nothing was found.
   */
  public PlaceDescription getPlaceWithName (String name) {
    for (PlaceDescription place : placeDescriptions) {
      if (place.getPlaceName().equals(name)) {
        return place;
      }
    }
    System.out.println("The provided name did not correspond to a Place Description in the " +
        "list. Returning null!");
    return null;
  }

  public ArrayList<String> getPlaceNames() {
    ArrayList<String> names = new ArrayList<>();
    for (PlaceDescription place : placeDescriptions) {
      names.add(place.getPlaceName());
    }
    return names;
  }

  public int getIndexOfPlaceWithName(String name) {
    for (int i = 0; i < placeDescriptions.size(); i++) {
      if (placeDescriptions.get(i).getPlaceName().equals(name)) {
        return i;
      }
    }
    return -1;
  }

  public boolean removePlaceWithName(String name) {
    for (int i = 0; i < placeDescriptions.size(); i++) {
      if (placeDescriptions.get(i).getPlaceName().equals(name)) {
        placeDescriptions.remove(i);
        return true;
      }
    }
    return false;
  }

  public boolean addPlace(PlaceDescription placeDescription) {
    placeDescriptions.add(placeDescription);
    return true;
  }

  public int size() {
    return placeDescriptions.size();
  }

  public String toJsonString() {
    JSONObject jObj = new JSONObject();
    JSONArray placeArray = new JSONArray();
    for (PlaceDescription place : placeDescriptions) {
      placeArray.put(place.toJsonObj());
    }
    try {
      jObj.put("placeArray", placeArray);
    } catch (JSONException e) {
      System.out.println("There was an error while trying to export the array of PlaceDescription " +
          "objects to the root JSON object");
      e.printStackTrace();
    }
    return jObj.toString();
  }

  /**
   * Imports messages from a JSONObject.
   * This will take the JSONObject, and extract each object from the array
   * into Message objects which will then be the PlaceDescriptions for the
   * PlaceLibrary.
   * @param jo is the JSONObject which holds the entire JSON for import
   */
  private void importJsonObject(JSONObject jo) {
    // Get the array of place descriptions in the JSON object.
    try {
      JSONArray jArr = (JSONArray) jo.get("placeArray");

      // Parse each entry of the array into an PlaceDescription object
      for (int i = 0; i < jArr.length(); i++) {
        JSONObject currentObj = (JSONObject) jArr.get(i);
        placeDescriptions.add(new PlaceDescription(currentObj.toString()));
      }

    } catch (org.json.JSONException e) {
      System.out.println("Error extracting the JSON array named 'placeArray'");
      System.out.println("Possibly a malformed JSON file was provided");
    }
  }


}
