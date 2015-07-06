package com.example.john.locationads;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FrequencyUpdater extends Service implements LocationProvider.LocationCallback,ReverseGeocoderTasker.GeolocationCallBack{


    private LocationProvider mLocationProvider;
    private DatabaseHandler db;
    private NodeManager mNodeManager;
    private static String TAG = "FrequencyUpdater";
    private SessionManager mSessionManager;
    private NetworkConnection mNetworkConnection;
    private PowerManager.WakeLock mWakeLock;
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private String jsonstring = " ";
    private JSONArray returned_locations;


    public FrequencyUpdater(){
    }

    @Override
        public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"Service Created");
    }

    @Override
    public void handleNewLocation(Location location) {
        ReverseGeocoderTasker task = new ReverseGeocoderTasker(this, location);
        task.execute();
    }

    @Override
    public void handlegeolocation(String locality_name,Location location) {
        location_frequency_updater(locality_name,location);
        new PollTask(location).execute();
    }

    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();

        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }

        mLocationProvider.connect();
        // do the actual work, in a separate thread

    }

    /**
     * This is deprecated, but you have to implement it if you're planning on
     * supporting devices with an API level lower than 5 (Android 2.0).
     */
    @Override
    public void onStart(Intent intent, int startId) {
        mLocationProvider = new LocationProvider(getApplicationContext(), this);
        mNetworkConnection = new NetworkConnection(getApplicationContext());
        db = new DatabaseHandler(this);
        mSessionManager = new SessionManager(getApplicationContext());
        mSessionManager.get_logged();
        mSessionManager.get_registered();
        mSessionManager.set_session();
        handleIntent(intent);

    }

    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service start", Toast.LENGTH_LONG).show();

        mSessionManager = new SessionManager(getApplicationContext());
        mSessionManager.get_logged();
        mSessionManager.get_registered();
        mSessionManager.set_session();

        mLocationProvider = new LocationProvider(getApplicationContext(), this);
        mNetworkConnection = new NetworkConnection(getApplicationContext());
        db = new DatabaseHandler(this);

//        SQLiteDatabase sql = db.getWritableDatabase();
//        db.onDrop(sql);

        handleIntent(intent);
        return START_STICKY;
    }

    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */

    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
        Log.i(TAG,"Wakelock released");
    }


    private class PollTask extends AsyncTask<Void, Void, Void> {

        private Location mlocation = null;

        public PollTask() {
        }

        public PollTask(Location mlocation) {
            this.mlocation = mlocation;
        }

        @Override
        protected Void doInBackground(Void... params){
            get_ad_location_sync(this.mlocation);
            return null;
        }

        /**
         * In here you should interpret whatever you fetched in doInBackground
         * and push any notifications you need to the status bar, using the
         * NotificationManager. I will not cover this here, go check the docs on
         * NotificationManager.
         *
         * What you HAVE to do is call stopSelf() after you've pushed your
         * notification(s). This will:
         * 1) Kill the service so it doesn't waste precious resources
         * 2) Call onDestroy() which will release the wake lock, so the device
         *    can go to sleep again and save precious battery.
         */

        @Override
        protected void onPostExecute(Void result) {
            build_notify_ad_locations(returned_locations);
//            stopSelf();
            mLocationProvider.connect();
        }
    }

    private void location_frequency_updater(String locality_name,Location location){
        Log.i(TAG,locality_name);
        Log.i(TAG, String.valueOf(location.getLatitude()));

        if (db.checkNode("place",locality_name) == true){
            mNodeManager = db.getNodebyPlace(locality_name);
            mNodeManager.set_freq(mNodeManager.get_freq()+1);
            db.updateNode(mNodeManager);
            Log.i(TAG,"Node present.... updating frequency");
        }
        else{
            db.addNode(new NodeManager(location.getLatitude(),location.getLongitude(),1,locality_name));
            Log.i(TAG,"new Node added");
        }

        stopSelf();
    }


    protected void build_notify_ad_locations(JSONArray jsonArray){
        String name=" ",snippet=" ";

        try {
            for (int i = 0; i<jsonArray.length();i++) {
                JSONObject adlocation = jsonArray.getJSONObject(i);
                name =  adlocation.getString("name");
                snippet = adlocation.getString("snippet");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Start", "notification");

      /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setContentTitle(name);
        mBuilder.setContentText(snippet);
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.mipmap.location_ad_icon);

      /* Increase notification number every time a new notification arrives */
//            mBuilder.setNumber(++numMessages);

      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private JSONObject get_ad_location_sync(Location location) {
        if (mNetworkConnection.internet_connection()) {
            if(GlobalVar.getUserEmail()!=null && GlobalVar.getUserToken()!=null) {
                String data_to_send = "lat=" + String.valueOf(location.getLatitude()) + "&lon=" + String.valueOf(location.getLongitude());
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet("http://stormy-brook-6865.herokuapp.com/api/v1/ads_manager?" + data_to_send);
                Log.i(TAG, "http://stormy-brook-6865.herokuapp.com/api/v1/ads_manager?" + data_to_send);

                httpget.setHeader("Content-type", "application/json");
                httpget.addHeader("X-User-Email", GlobalVar.getUserEmail());
                httpget.addHeader("X-User-Token", GlobalVar.getUserToken());

                InputStream inputstream = null;
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    inputstream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    jsonstring = sb.toString();
                    JSONObject jObject = new JSONObject(jsonstring);
                    Log.i(TAG, jsonstring);

                    JSONArray jArray = jObject.getJSONArray("adlocation");
                    returned_locations = jArray;

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } else
                Log.i(TAG,"You are not logged in.");
//                Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG,"No data connection found");
//            Toast.makeText(getApplicationContext(), " No data connection found", Toast.LENGTH_LONG).show();
        }

        return null;
    }

}//Service class....
