package edu.asu.bsse.aneuhold.places;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Copyright (c) 2018 Tim Lindquist,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: Example Android application that uses an AsyncTask to accomplish the same effect
 * as using a Thread and android.os.Handler
 *
 * Ser423 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version December 3, 2018
 *
 * -------------------------
 *
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
 * Purpose: Provides the framework for requesting JSON RPC methods and data over HTTP.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 6, 2019
 */
public class JsonRPCRequestViaHttp {

  private final Map<String, String> headers;
  private URL url;
  private String requestData;
  private MainActivity parent;

  public JsonRPCRequestViaHttp(URL url, MainActivity parent) {
    this.url = url;
    this.parent = parent;
    this.headers = new HashMap<>();
  }

  public void setHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public String call(String requestData) throws Exception {
    android.util.Log.d(this.getClass().getSimpleName(),"in call, url: "+url.toString()+" requestData: "+requestData);
    return post(url, headers, requestData);
  }

  private String post(URL url, Map<String, String> headers, String data) throws Exception {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    this.requestData = data;
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.addRequestProperty(entry.getKey(), entry.getValue());
      }
    }
    connection.addRequestProperty("Accept-Encoding", "gzip");
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    connection.connect();
    try (OutputStream out = connection.getOutputStream()) {
      out.write(data.getBytes());
      out.flush();
      out.close();
      int statusCode = connection.getResponseCode();
      if (statusCode != HttpURLConnection.HTTP_OK) {
        throw new Exception(
            "Unexpected status from post: " + statusCode);
      }
    }
    String responseEncoding = connection.getHeaderField("Content-Encoding");
    responseEncoding = (responseEncoding == null ? "" : responseEncoding.trim());
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    InputStream in = connection.getInputStream();
    try {
      in = connection.getInputStream();
      if ("gzip".equalsIgnoreCase(responseEncoding)) {
        in = new GZIPInputStream(in);
      }
      in = new BufferedInputStream(in);
      byte[] buff = new byte[1024];
      int n;
      while ((n = in.read(buff)) > 0) {
        bos.write(buff, 0, n);
      }
      bos.flush();
      bos.close();
    } finally {
      if (in != null) {
        in.close();
      }
    }
    android.util.Log.d(this.getClass().getSimpleName(),"json rpc request via http returned string "+bos.toString());
    return bos.toString();
  }
}
