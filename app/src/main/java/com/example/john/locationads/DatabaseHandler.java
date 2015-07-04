package com.example.john.locationads;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "AdLocationManager";

    // table name
    private static final String TABLE_FREQ = "freq_manager";
    private static final String TABLE_NODE = "node_manager";

    //freq_manager column names
    private static final String KEY_ID= "id";
    private static final String KEY_NODE_ID= "node_id";
    private static final String KEY_START_LAT = "start_point_lat";
    private static final String KEY_START_LON = "start_point_lon";
    private static final String KEY_END_LAT = "end_point_lat";
    private static final String KEY_END_LON = "end_point_lon";
    private static final String KEY_FREQ = "freq";

    //node_manager column names
    private static final String KEY_ID_NODE = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_FREQ_NODE = "freq";
    private static final String KEY_PLACE = "place";

    private static final float RADIUS = 100;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FREQ_TABLE1 = "CREATE TABLE " + TABLE_FREQ + "(" + KEY_ID + " INTEGER PRIMARY KEY,"+ KEY_NODE_ID + " INTEGER," + KEY_START_LAT + " DOUBLE," + KEY_START_LON + " DOUBLE," + KEY_END_LAT + " DOUBLE," + KEY_END_LON + " DOUBLE," + KEY_FREQ + " INTEGER" + ")";
        String CREATE_FREQ_TABLE2 = "CREATE TABLE " + TABLE_NODE + "(" + KEY_ID_NODE + " INTEGER PRIMARY KEY,"+ KEY_LAT + " DOUBLE," + KEY_LNG + " DOUBLE," + KEY_FREQ + " INTEGER," +  KEY_PLACE + " TEXT" + ")";
        db.execSQL(CREATE_FREQ_TABLE1);
        db.execSQL(CREATE_FREQ_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_FREQ + "','" + TABLE_NODE + "'");
        // Create tables again
        onCreate(db);
    }

    public void onDrop(SQLiteDatabase db){
        Log.i(TAG,"drop database");

        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_NODE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_FREQ + "'");

        // Create tables again
        onCreate(db);
    }

    //add new freq
    public void addFreq(FreqManager mFreqManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, mFreqManager.get_id());
        values.put(KEY_NODE_ID, mFreqManager.get_node_id());
        values.put(KEY_START_LAT, mFreqManager.get_start_point_lat());
        values.put(KEY_START_LON, mFreqManager.get_start_point_lon());
        values.put(KEY_END_LAT, mFreqManager.get_end_point_lat());
        values.put(KEY_END_LON, mFreqManager.get_end_point_lon());
        values.put(KEY_FREQ, mFreqManager.get_freq());

        // Inserting Row
        db.insert(TABLE_FREQ, null, values);
        db.close(); // Closing database connection
    }

    // Getting single freq details
    public FreqManager getFreq(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FREQ, new String[] { KEY_ID,KEY_NODE_ID,KEY_START_LAT,KEY_START_LON,KEY_END_LAT,KEY_END_LON, KEY_FREQ }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FreqManager mFreqManager = new FreqManager(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),
                Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3)),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)), Integer.parseInt(cursor.getString(6)));

        return mFreqManager;
    }

    // Getting All Contacts
    public List<FreqManager> getAllFreq() {
        List<FreqManager> freqList = new ArrayList<FreqManager>();

        String selectQuery = "SELECT  * FROM " + TABLE_FREQ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FreqManager mFreqManager = new FreqManager();
                mFreqManager.set_id(Integer.parseInt(cursor.getString(0)));
                mFreqManager.set_node_id(Integer.parseInt(cursor.getString(1)));
                mFreqManager.set_start_point_lat(Double.parseDouble(cursor.getString(2)));
                mFreqManager.set_start_point_lon(Double.parseDouble(cursor.getString(3)));
                mFreqManager.set_end_point_lat(Double.parseDouble(cursor.getString(4)));
                mFreqManager.set_end_point_lon(Double.parseDouble(cursor.getString(5)));
                mFreqManager.set_freq(Integer.parseInt(cursor.getString(6)));
                // Adding contact to list
                freqList.add(mFreqManager);
            } while (cursor.moveToNext());
        }

        // return contact list
        return freqList;

    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FREQ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();

    }

    // Updating single freq
    public int updateFreq(FreqManager mFreqManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NODE_ID, mFreqManager.get_node_id());
        values.put(KEY_START_LAT, mFreqManager.get_start_point_lat());
        values.put(KEY_START_LAT, mFreqManager.get_start_point_lat());
        values.put(KEY_END_LAT, mFreqManager.get_end_point_lat());
        values.put(KEY_END_LON, mFreqManager.get_end_point_lon());
        values.put(KEY_FREQ, mFreqManager.get_freq());

        // updating row
        return db.update(TABLE_FREQ, values, KEY_ID + " = ?",
                new String[] { String.valueOf(mFreqManager.get_id()) });
    }

    // Deleting single freq by primary id
    public void deleteFreq(FreqManager mFreqManager) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FREQ, KEY_ID + " = ?",
                new String[] { String.valueOf(mFreqManager.get_id()) });
        db.close();
    }

    //Deleting row by node_id
    public void deleteFreqByNode(FreqManager mFreqManager) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FREQ, KEY_NODE_ID + " = ?",
                new String[] { String.valueOf(mFreqManager.get_node_id()) });
        db.close();
    }


/*-------------------------------------------------------- NODE TABLE ------------------------------------------------------------------------------------------------------------------------*/

    //add new node
    public void addNode(NodeManager mNodeManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LAT, mNodeManager.get_lat());
        values.put(KEY_LNG, mNodeManager.get_lng());
        values.put(KEY_FREQ_NODE, mNodeManager.get_freq());
        values.put(KEY_PLACE, mNodeManager.get_place());

        // Inserting Row
        db.insert(TABLE_NODE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single node details
    public NodeManager getNode(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NODE, new String[] { KEY_ID_NODE,KEY_LAT,KEY_LNG,KEY_FREQ_NODE,KEY_PLACE}, KEY_ID_NODE + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        NodeManager mNodeManager = new NodeManager(Integer.parseInt(cursor.getString(0)),Double.parseDouble(cursor.getString(1)),Double.parseDouble(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4));

        return mNodeManager;
    }


    public NodeManager getNodebyPlace(String place) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NODE, new String[] { KEY_ID_NODE,KEY_LAT,KEY_LNG,KEY_FREQ_NODE,KEY_PLACE}, KEY_PLACE + "=?",
                new String[] { place }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        NodeManager mNodeManager = new NodeManager(Integer.parseInt(cursor.getString(0)),Double.parseDouble(cursor.getString(1)),Double.parseDouble(cursor.getString(2)),Integer.parseInt(cursor.getString(3)),cursor.getString(4));

        return mNodeManager;
    }

    public void deleteNode(NodeManager mNodeManager) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NODE, KEY_ID_NODE + " = ?",
                new String[] { String.valueOf(mNodeManager.get_id()) });
        db.close();
    }

    // Updating single node
    public int updateNode(NodeManager mNodeManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LAT, mNodeManager.get_lat());
        values.put(KEY_LNG, mNodeManager.get_lng());
        values.put(KEY_FREQ_NODE, mNodeManager.get_freq());
        values.put(KEY_PLACE, mNodeManager.get_place());

        // updating row
        return db.update(TABLE_NODE, values, KEY_ID_NODE + " = ?",
                new String[] { String.valueOf(mNodeManager.get_id()) });
    }

    public boolean checkNode(String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NODE + " where " + dbfield + " = '" + fieldValue + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public static PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }

    public static double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.x - p1.x);
        double dLon = Math.toRadians(p2.y - p1.y);
        double lat1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.x);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    public static boolean pointIsInCircle(PointF pointForCheck, PointF center) {
        if (getDistanceBetweenTwoPoints(pointForCheck, center) <= RADIUS)
            return true;
        else
            return false;
    }

    public NodeManager getnearbyNode(float x, float y,String type) {
        List<NodeManager> NodeList = new ArrayList<NodeManager>();
        float radius = RADIUS;
        PointF center = new PointF(x, y);
        final double mult = 1.1;
        PointF p1 = calculateDerivedPosition(center, mult * radius, 0);
        PointF p2 = calculateDerivedPosition(center, mult * radius, 90);
        PointF p3 = calculateDerivedPosition(center, mult * radius, 180);
        PointF p4 = calculateDerivedPosition(center, mult * radius, 270);

        String selectQuery;
        if(type == "source"){
           selectQuery = "SELECT  * FROM " + TABLE_NODE + " WHERE "
                + KEY_LAT + " > " + String.valueOf(p3.x) + " AND "
                + KEY_LAT + " < " + String.valueOf(p1.x) + " AND "
                + KEY_LNG + " < " + String.valueOf(p2.y) + " AND "
                + KEY_LNG + " > " + String.valueOf(p4.y);}
        else{
           selectQuery = "SELECT  * FROM " + TABLE_NODE + " WHERE "
                + KEY_LAT + " > " + String.valueOf(p3.x) + " AND "
                + KEY_LAT + " < " + String.valueOf(p1.x) + " AND "
                + KEY_LNG + " < " + String.valueOf(p2.y) + " AND "
                + KEY_LNG + " > " + String.valueOf(p4.y);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NodeManager mNodeManager = new NodeManager();
                mNodeManager.set_id(Integer.parseInt(cursor.getString(0)));
                mNodeManager.set_lat(Double.parseDouble(cursor.getString(1)));
                mNodeManager.set_lng(Double.parseDouble(cursor.getString(2)));
                mNodeManager.set_freq(Integer.parseInt(cursor.getString(3)));
                mNodeManager.set_place(cursor.getString(4));

                // Adding contact to list
                NodeList.add(mNodeManager);
            } while (cursor.moveToNext());
        }

        for (int i = 0; i < NodeList.size(); i++)
            if(pointIsInCircle(new PointF(Float.parseFloat(NodeList.get(i).get_lat().toString()),Float.parseFloat(NodeList.get(i).get_lng().toString())),center) == true){
               return NodeList.get(i);
            }
        return null;
    }

}
