package edu.asu.bsse.aneuhold.places;

import android.app.Activity;

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
 * Purpose: Provides a convenient class to enclose method information for passing to an
 * AsyncPlaceConnect object.
 *
 * The different methods that can be used are as follows:
 *
 * - get
 * -- <code>params</code> should be a String[] type with one String which is the name of the place.
 * - add
 * - getNames
 * -- <code>params</code> should be a new Object[]{} with nothing in it
 * - resetFromJsonFile
 * - saveToJsonFile
 * - remove
 * -- <code>params</code> should be a String[] type with one String which is the name of the place.
 * - getCategoryNames
 * - getNamesInCategory
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 10, 2019
 */
public class RPCMethodInformation {
  public String method;
  public Object[] params;
  public MainActivity mainActivity;
  public String urlString;
  public String resultAsJson;
  public String callbackMethodName;
  public HashMap<String, Object> extra;

  /**
   * callingActivity is an optional setting that can be set after creating a new RPCMethodInformation
   * object.
   */
  public Activity callingActivity;

  RPCMethodInformation(MainActivity mainActivity, String urlString, String method, Object[] params){
    this.method = method;
    this.mainActivity = mainActivity;
    this.urlString = urlString;
    this.params = params;
    this.resultAsJson = "{}";
    this.callingActivity = null;
    this.callbackMethodName = null;
    this.extra = new HashMap<>();
  }
}
