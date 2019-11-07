package edu.asu.bsse.aneuhold.places;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

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
 * @version November 6, 2019
 */
public class AsyncPlacesConnect extends AsyncTask<RPCMethodInformation, Integer, RPCMethodInformation> {

  @Override
  protected void onPreExecute(){
    android.util.Log.d(this.getClass().getSimpleName(),"in onPreExecute on "+
        (Looper.myLooper() == Looper.getMainLooper()?"Main thread":"Async Thread"));
  }

  /**
   * Override this method to perform a computation on a background thread. The
   * specified parameters are the parameters passed to {@link #execute}
   * by the caller of this task.
   * <p>
   * This will normally run on a background thread. But to better
   * support testing frameworks, it is recommended that this also tolerates
   * direct execution on the foreground thread, as part of the {@link #execute} call.
   * <p>
   * This method can call {@link #publishProgress} to publish updates
   * on the UI thread.
   *
   * @param rpcMethodInformations The parameters of the task.
   * @return A result, defined by the subclass of this task.
   * @see #onPreExecute()
   * @see #onPostExecute
   * @see #publishProgress
   */
  @Override
  protected RPCMethodInformation doInBackground(RPCMethodInformation... rpcMethodInformations) {

    // array of methods to be called. Assume exactly one input, a single RPCMethodInformation object
    android.util.Log.d(this.getClass().getSimpleName(),"in doInBackground on "+
        (Looper.myLooper() == Looper.getMainLooper()?"Main thread":"Async Thread"));
    try {
      JSONArray ja = new JSONArray(rpcMethodInformations[0].params);
      android.util.Log.d(this.getClass().getSimpleName(),"params: "+ja.toString());
      String requestData = "{ \"jsonrpc\":\"2.0\", \"method\":\""+rpcMethodInformations[0].method
          + "\", \"params\":" + ja.toString() + ",\"id\":3}";

      // Log the request
      android.util.Log.d(this.getClass().getSimpleName(),"requestData: "+requestData+" url: "
          + rpcMethodInformations[0].urlString);

      // Make the request
      JsonRPCRequestViaHttp conn = new JsonRPCRequestViaHttp(
          (new URL(rpcMethodInformations[0].urlString)),
          rpcMethodInformations[0].mainActivity
      );
      String resultStr = conn.call(requestData);
      rpcMethodInformations[0].resultAsJson = resultStr;
    } catch (Exception ex){
      android.util.Log.d(this.getClass().getSimpleName(),"exception in remote call "+
          ex.getMessage());
    }
    return rpcMethodInformations[0];
  }

  /**
   * Performs various actions based on the method property of the returned RPCMethodInformation
   * object. See the RPCMethodInformation class for the different method options.
   * @param res
   */
  @Override
  protected void onPostExecute(RPCMethodInformation res) {

    // Logging
    Log.d(this.getClass().getSimpleName(), "in onPostExecute on " +
        (Looper.myLooper() == Looper.getMainLooper() ? "Main thread" : "Async Thread"));
    Log.d(this.getClass().getSimpleName(), " resulting is: " + res.resultAsJson);

    try {

      if (res.method.equals("getNames")) {
        JSONObject jo = new JSONObject(res.resultAsJson);
        JSONArray ja = jo.getJSONArray("result");
        ArrayList<String> al = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
          al.add(ja.getString(i));
        }
        String[] names = al.toArray(new String[0]);

        // Update the mainActivity and the recyclerViewAdapter placeNames
        res.mainActivity.placeNames = names;
        res.mainActivity.recyclerViewAdapter.placeNames = names;
        res.mainActivity.recyclerViewAdapter.notifyDataSetChanged();
      }

      /*
       * Should only be called from within the PlaceDetailsActivity class.
       */
      else if (res.method.equals("get")) {
        JSONObject jo = new JSONObject(res.resultAsJson);
        PlaceDescription place = new PlaceDescription(jo.getJSONObject("result"));
        PlaceDetailsActivity placeDetailsActivity = (PlaceDetailsActivity) res.callingActivity;
        placeDetailsActivity.placeDescription = place;
        placeDetailsActivity.hydrateTextFields();
      }
      else if (res.method.equals("add")) {
        // add Method

        // Finished adding a place. Refresh the list of places by going back to the server for names
        try {
          RPCMethodInformation mi = new RPCMethodInformation(res.mainActivity, res.urlString,
              "getNames", new Object[]{});
          new AsyncPlacesConnect().execute(mi);
        } catch (Exception ex) {
          Log.w(this.getClass().getSimpleName(), "Exception processing getNames: " +
              ex.getMessage());
        }

      }
      else if (res.method.equals("delete")) {
        if (res.mainActivity.waitingForDelete) {
          res.mainActivity.addPlaceAfterDeletion();
        } else {

          // Refresh the list of places
          RPCMethodInformation mi = new RPCMethodInformation(res.mainActivity, res.urlString,
              "getNames", new Object[]{});
          new AsyncPlacesConnect().execute(mi);
        }
      }
    }catch (Exception ex){
      Log.d(this.getClass().getSimpleName(),"Exception: "+ex.getMessage());
    }
  }
}
