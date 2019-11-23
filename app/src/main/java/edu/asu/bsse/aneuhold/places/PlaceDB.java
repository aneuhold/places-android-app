package edu.asu.bsse.aneuhold.places;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
 * Purpose: Initializes the database and copies it over to the user's local storage if necessary
 * from the bundle. This way edits are possible. The database has one table named 'place' with the
 * following fields:
 *
 * 0: name TEXT PRIMARY KEY,
 * 1: description TEXT,
 * 2: category TEXT,
 * 3: address_title TEXT,
 * 4: address_street TEXT,
 * 5: elevation DOUBLE PRECISION,
 * 6: latitude DOUBLE PRECISION,
 * 7: longitude DOUBLE PRECISION
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 10, 2019
 */
public class PlaceDB extends SQLiteOpenHelper {
  private static final boolean DEBUG_ON = true;
  private static final int DATABASE_VERSION = 1;
  private static String dbName = "placedb";
  private String dbPath;
  private SQLiteDatabase placeDB;
  private final Context context;

  public PlaceDB(Context context) {
    super(context,dbName, null, DATABASE_VERSION);
    this.context = context;

    // place the database in the files directory. Could also place it in the databases directory
    // with dbPath = context.getDatabasePath("dbName"+".db").getPath();
    dbPath = context.getFilesDir().getPath()+"/";
    android.util.Log.d(this.getClass().getSimpleName(),"db path is: "+
        context.getDatabasePath("coursedb"));
    android.util.Log.d(this.getClass().getSimpleName(),"dbpath: "+dbPath);
  }

  /**
   * Called when the database is created for the first time. This is where the
   * creation of tables and the initial population of the tables should happen.
   *
   * @param db The database.
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    /*
     Empty at the moment. Instructor provided solution was showing to create a new DB every time
     an action occurred in the app.
    */
  }

  /**
   * Called when the database needs to be upgraded. The implementation
   * should use this method to drop tables, add tables, or do anything else it
   * needs to upgrade to the new schema version.
   *
   * <p>
   * The SQLite ALTER TABLE documentation can be found
   * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
   * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
   * you can use ALTER TABLE to rename the old table, then create the new table and then
   * populate the new table with the contents of the old table.
   * </p><p>
   * This method executes within a transaction.  If an exception is thrown, all changes
   * will automatically be rolled back.
   * </p>
   *
   * @param db         The database.
   * @param oldVersion The old database version.
   * @param newVersion The new database version.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Left empty at the moment.
  }

  /**
   * Does the database exist and has it been initialized? This method determines whether
   * the database needs to be copied to the data/data/pkgName/files directory by
   * checking whether the file exists. If it does it checks to see whether the db is
   * uninitialized or whether it has the place table.
   *
   * @return false if the database file needs to be copied from the assets directory, true
   * otherwise.
   */
  private boolean checkDB() {
    SQLiteDatabase checkDB = null;
    boolean placeTableExists = false;
    try{
      String path = dbPath + dbName + ".db";
      debug("PlaceDB --> checkDB: path to db is", path);
      File aFile = new File(path);
      if (aFile.exists()) {
        checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        if (checkDB!=null) {
          debug("PlaceDB --> checkDB", "opened db at: " + checkDB.getPath());
          Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name='place';", null);
          if (tabChk == null) {
            debug("PlaceDB --> checkDB", "check for place table result set is null");
          } else {
            tabChk.moveToNext();
            debug("PlaceDB --> checkDB", "check for place table result set is: " +
                ((tabChk.isAfterLast() ? "empty" : tabChk.getString(0))));
            placeTableExists = !tabChk.isAfterLast();
          }
          if (placeTableExists) {
            Cursor c = checkDB.rawQuery("SELECT * FROM place", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
              String crsName = c.getString(0);
              int crsid = c.getInt(1);
              debug("PlaceDB --> checkDB", "Place table has CourseName: " +
                  crsName + "\tname: " + crsid);
              c.moveToNext();
            }
            placeTableExists = true;
            c.close();
          }
          if (tabChk != null) tabChk.close();
        }
      }
    }catch(SQLiteException e){
      android.util.Log.w("CourseDB->checkDB",e.getMessage());
    }
    if(checkDB != null){
      checkDB.close();
    }
    return placeTableExists;
  }

  private void copyDB() {
    try {
      if(!checkDB()){

        // only copy the database if it doesn't already exist in my database directory
        debug("CourseDB --> copyDB", "checkDB returned false, starting copy");
        InputStream ip =  context.getResources().openRawResource(R.raw.placedb);

        // make sure the database path exists. if not, create it.
        File aFile = new File(dbPath);
        if(!aFile.exists()){
          aFile.mkdirs();
        }
        String op = dbPath + dbName + ".db";
        OutputStream output = new FileOutputStream(op);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = ip.read(buffer))>0){
          output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        ip.close();
      }
    } catch (IOException e) {
      android.util.Log.w("PlaceDB --> copyDB", "IOException: "+e.getMessage());
    }
  }

  public SQLiteDatabase openDB() {
    String myPath = dbPath + dbName + ".db";
    if(checkDB()) {
      placeDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
      debug("PlaceDB --> openDB", "opened db at path: " + placeDB.getPath());
    }else{
      try {
        this.copyDB();
        placeDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
      }catch(Exception ex) {
        Log.w(this.getClass().getSimpleName(),"unable to copy and open db: "+ex.getMessage());
      }
    }
    return placeDB;
  }

  private void debug(String hdr, String msg){
    if(DEBUG_ON){
      android.util.Log.d(hdr,msg);
    }
  }

  public static void deletePlaceFromDB(String name, Context context) {
    try (SQLiteDatabase placeDB = new PlaceDB(context).openDB()) {
      placeDB.delete("place", "name=?", new String[]{name});
    } catch (Exception e) {
      System.out.println("Error in deletePlaceFromDB method");
      e.printStackTrace();
    }
  }

  public static String[] getPlaceNamesFromDB(Context context) {
    String[] placeNames = new String[]{"Loading places..."};
    try (SQLiteDatabase placeDB = new PlaceDB(context).openDB()) {
      Cursor namesCursor = placeDB.rawQuery("SELECT name FROM place", null);
      placeNames = new String[namesCursor.getCount()];
      namesCursor.moveToFirst();
      for (int i = 0; i < namesCursor.getCount(); i++) {
        placeNames[i] = namesCursor.getString(0);
        namesCursor.moveToNext();
      }
      namesCursor.close();
    } catch (Exception e) {
      System.out.println("Error in updatePlaceNamesFromDB method");
      e.printStackTrace();
    }
    return placeNames;
  }

  public static PlaceDescription getPlaceDescriptionFromDB(String placeName, Context context) {
    PlaceDescription placeDescription = new PlaceDescription();
    try (SQLiteDatabase placeDB = new PlaceDB(context).openDB()) {
      Cursor cursor = placeDB.rawQuery("SELECT * FROM place WHERE name=?",
          new String[]{placeName});
      cursor.moveToFirst();
      placeDescription.setPlaceName(placeName);

      // Pull the data from the cursor to the PlaceDescription object
      placeDescription.setPlaceDescription(cursor.getString(1));
      placeDescription.setCategory(cursor.getString(2));
      placeDescription.setAddressTitle(cursor.getString(3));
      placeDescription.setAddressStreet(cursor.getString(4));
      placeDescription.setElevation(cursor.getDouble(5));
      placeDescription.setLatitude(cursor.getDouble(6));
      placeDescription.setLongitude(cursor.getDouble(7));

      System.out.println("The latitude is: " + cursor.getDouble(6));

      cursor.close();
    } catch (Exception e) {
      System.out.println("Error in getPlaceDescriptionFromDB method");
      e.printStackTrace();
    }
    return placeDescription;
  }

  public static void updatePlaceDescriptionInDB(PlaceDescription updatedPlaceDescription, Context context) {
    String placeName = updatedPlaceDescription.getPlaceName();
    try (SQLiteDatabase placeDB = new PlaceDB(context).openDB()) {
      ContentValues newValue = new ContentValues();
      newValue.put("description", updatedPlaceDescription.getPlaceDescription());
      newValue.put("category", updatedPlaceDescription.getCategory());
      newValue.put("address_title", updatedPlaceDescription.getAddressTitle());
      newValue.put("address_street", updatedPlaceDescription.getAddressStreet());
      newValue.put("elevation", updatedPlaceDescription.getElevation());
      newValue.put("latitude", updatedPlaceDescription.getLatitude());
      newValue.put("longitude", updatedPlaceDescription.getLongitude());

      // Update the value in the place table
      placeDB.update("place", newValue, "name=?", new String[]{placeName});

    } catch (Exception e) {
      System.out.println("Error in updatePlaceDescriptionInDB method");
      e.printStackTrace();
    }
  }

  public static void addPlaceInDB(PlaceDescription placeDescription, Context context) {
    try (SQLiteDatabase placeDB = new PlaceDB(context).openDB()) {
      ContentValues newValue = new ContentValues();
      newValue.put("name", placeDescription.getPlaceName());
      newValue.put("description", placeDescription.getPlaceDescription());
      newValue.put("category", placeDescription.getCategory());
      newValue.put("address_title", placeDescription.getAddressTitle());
      newValue.put("address_street", placeDescription.getAddressStreet());
      newValue.put("elevation", placeDescription.getElevation());
      newValue.put("latitude", placeDescription.getLatitude());
      newValue.put("longitude", placeDescription.getLongitude());

      // Insert the value into the place table
      placeDB.insert("place", null, newValue);

    } catch (Exception e) {
      System.out.println("Error in updatePlaceDescriptionInDB method");
      e.printStackTrace();
    }
  }
}
