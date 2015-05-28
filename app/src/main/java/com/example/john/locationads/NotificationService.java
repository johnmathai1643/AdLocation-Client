package com.example.john.locationads;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

public class NotificationService extends Service {

    public static final String TAG = MainActivity.class.getSimpleName();
//    public static Location LOCATION_CURRENT;
//    private LocationProvider mLocationProvider;
//    private NetworkConnection mNetworkConnection;
    private NotificationManager mNotificationManager;
    private LocationManager mLocationManager;
    private int notificationID = 100;
    private Handler h;
    private PowerManager.WakeLock mWakeLock;
    JSONArray returned_locations;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        // do the actual work, in a separate thread
//        new PollTask().execute();
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mLocationListener = new LocationListenerAds();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);

    }

    private class PollTask extends AsyncTask<Void, Void, Void> {
        public PollTask() {

        }

        @Override
        protected Void doInBackground(Void... params){

            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            LocationListener mLocationListener = new LocationListenerAds();
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);

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
            stopSelf();
        }
    }

    /**
     * This is deprecated, but you have to implement it if you're planning on
     * supporting devices with an API level lower than 5 (Android 2.0).
     */
    @Override
    public void onStart(Intent intent, int startId) {
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
    }

    private class LocationListenerAds implements LocationListener {
        @Override
        public void onLocationChanged(final Location location) {

            h = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    build_notify_ad_locations(returned_locations);
                }
            };

             Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        get_ad_location_sync(location);
                        h.sendEmptyMessage(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th.start();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private JSONObject get_ad_location_sync(Location location){
        String jsonstring;
        double currentLatitude = 13;
        double currentLongitude = 80;

        String data_to_send = "lat="+String.valueOf(currentLatitude)+"&lon="+String.valueOf(currentLongitude);
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet httppost = new HttpGet("http://stark-lake-4080.herokuapp.com/api/ads_manager?"+data_to_send);
        Log.i(TAG,"http://stark-lake-4080.herokuapp.com/api/ads_manager?"+data_to_send);
        httppost.setHeader("Content-type", "application/json");
        InputStream inputstream = null;
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputstream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!=null){
                sb.append(line + "\n");
            }
            jsonstring = sb.toString();
            JSONObject jObject = new JSONObject(jsonstring);
            Log.i(TAG,jsonstring);

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
    }

}