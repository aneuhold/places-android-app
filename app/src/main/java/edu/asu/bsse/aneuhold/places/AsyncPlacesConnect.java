
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
 * Purpose: Provides a way to connect to the JSON RPC server for the app in background threads.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 23, 2019
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
      rpcMethodInformations[0].resultAsJson = conn.call(requestData);
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
    Log.d(this.getClass().getSimpleName(), "Response result is: " + res.resultAsJson);

    try {

      switch (res.method) {
        case "getNames": {
          JSONObject jo = new JSONObject(res.resultAsJson);
          JSONArray ja = jo.getJSONArray("result");
          ArrayList<String> al = new ArrayList<>();
          for (int i = 0; i < ja.length(); i++) {
            al.add(ja.getString(i));
          }
          String[] names = al.toArray(new String[0]);

          switch (res.callbackMethodName) {
            case "syncDBWithJsonServerCallback":
              res.mainActivity.syncDBWithJsonServerCallback(names);
              break;
            case "syncLocalDBToJsonServerCallback":
              res.mainActivity.syncLocalDBToJsonServerCallback(names);
              break;
            case "syncJSonServerToLocalDBCallback":
              res.mainActivity.syncJSonServerToLocalDBCallback(names);
              break;
          }

          break;
        }
        case "get": {

          JSONObject jo = new JSONObject(res.resultAsJson);
          PlaceDescription place = new PlaceDescription(jo.getJSONObject("result"));

          if (res.callbackMethodName.equals("PlaceDB.addPlaceInDB")) {
            PlaceDB.addPlaceInDB(place, res.mainActivity);
            res.mainActivity.placeNames.add(place.getPlaceName());
            res.mainActivity.recyclerViewAdapter.notifyDataSetChanged();
          }

          break;
        }
        case "add":
          // Empty for now
          break;
        case "remove":
          if (res.callbackMethodName != null && res.callbackMethodName.equals("add")) {
            PlaceDescription placeDescription =
                PlaceDB.getPlaceDescriptionFromDB((String) res.extra.get("addBack"), res.mainActivity);
            RPCMethodInformation mi = new RPCMethodInformation(res.mainActivity,
                res.mainActivity.getResources().getString(R.string.server_url_string),
                "add",
                new Object[]{placeDescription.toJsonObj()});
            new AsyncPlacesConnect().execute(mi);
          }
          break;
      }
    }catch (Exception ex){
      Log.d(this.getClass().getSimpleName(),"Exception: "+ex.getMessage());
    }
  }
}
