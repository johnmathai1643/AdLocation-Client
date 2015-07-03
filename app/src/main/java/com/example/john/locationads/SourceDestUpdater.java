package com.example.john.locationads;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SourceDestUpdater extends Service{

    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    private LocationProvider mLocationProvider;
    private DatabaseHandler db;
    private NodeManager mNodeManager;
    private static String TAG = "SourceDestUpdater";
    private String get_reverse_geocoded_location = null;
    private static long TIME_DIFF = 2*3600; // in seconds

    public SourceDestUpdater(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }

        db = new DatabaseHandler(getApplicationContext());
//        mLocationProvider = new LocationProvider(getApplicationContext(), this);
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(getApplicationContext()), 0, NOTIFY_INTERVAL);

    }

    class TimeDisplayTimerTask extends TimerTask implements LocationProvider.LocationCallback{

        public TimeDisplayTimerTask(Context applicationContext) {
            mLocationProvider = new LocationProvider(getApplicationContext(),this);
        }

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("Insert: ", "Inserting ..");
//                    source_destination_updater();
                    Toast.makeText(getApplicationContext(), getDateTime(),
                            Toast.LENGTH_SHORT).show();
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

        private void source_destination_updater(){
            mNodeManager = db.getnearbyNode((float) mLocationProvider.get_location_preference().getLatitude(), (float) mLocationProvider.get_location_preference().getLongitude(),"source");

            while(get_reverse_geocoded_location == null){
                get_reverse_geocoded_location = reversegeocode(mLocationProvider.get_location_preference());
                if (get_reverse_geocoded_location != null)
                    break;
            }

            if (mNodeManager != null) {
                long source_time_diff = mNodeManager.get_source_time() - System.currentTimeMillis();
                long dest_time_diff = mNodeManager.get_dest_time() - System.currentTimeMillis();

                if (mLocationProvider.get_location_preference().distanceTo(mNodeManager.get_source_node_location()) > 200) {

                    mNodeManager.set_dest_point_lat(mLocationProvider.get_location().getLatitude());
                    mNodeManager.set_dest_point_lon(mLocationProvider.get_location().getLongitude());
                    mNodeManager.set_source_time(System.currentTimeMillis());
                    mNodeManager.set_freq(mNodeManager.get_freq() + 1);
                    mNodeManager.set_source_place(get_reverse_geocoded_location);
                    mNodeManager.set_dest_place(get_reverse_geocoded_location);
                    db.updateNode(mNodeManager);

                } else if (dest_time_diff > TIME_DIFF) {
                    Log.i(TAG, "add due to time difference");
                    db.addNode(new NodeManager(mNodeManager.get_dest_point_lat(), mNodeManager.get_dest_point_lon(), mLocationProvider.get_location().getLatitude(), mLocationProvider.get_location().getLongitude(), 1, System.currentTimeMillis(), System.currentTimeMillis(), null, null));
                } else {
                    Log.i(TAG, "update failed");
                }

            }
            else
                Log.i(TAG,"Failed:mNodeManager returned null");

        }

        private String reversegeocode(Location currentLocation){

            if (currentLocation != null)
            {
                // Kickoff an asynchronous task to fire the reverse geocoding
                // request off to google
                ReverseGeocoderTasker task = new ReverseGeocoderTasker(currentLocation);
                task.execute();
            }

            if (get_reverse_geocoded_location != null)
               return get_reverse_geocoded_location;
            else
               return null;
        }

        @Override
        public void handleNewLocation(Location location) {
            Log.i(TAG,"TAAAAAAAGGGG");
            Toast.makeText(getApplicationContext(),"TAAAAAAGGGGGGGGG",Toast.LENGTH_LONG);
            source_destination_updater();
        }
    } //TimerTask Class


 /************************************************** Async class ****************************************************///
    class ReverseGeocoderTasker extends AsyncTask<Void, Void, String> {

        private Location currentLocation;
        private String localityName;

        public ReverseGeocoderTasker(){
        }

        public ReverseGeocoderTasker(Location currentLocation){
            this.currentLocation = currentLocation;
        }

        @Override
        protected String doInBackground(Void... params) {

            if (this.currentLocation != null)
            {
                this.localityName = Geocoder.reverseGeocode(currentLocation);
            }

            return this.localityName;
        }

        @Override
        protected void onPostExecute(String result)
        {   super.onPostExecute(result);
            get_reverse_geocoded_location = result;
        }

    }



}//Service class....
