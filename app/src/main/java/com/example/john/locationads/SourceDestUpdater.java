package com.example.john.locationads;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class SourceDestUpdater extends Service implements LocationProvider.LocationCallback,ReverseGeocoderTasker.GeolocationCallBack{


    private LocationProvider mLocationProvider;
    private DatabaseHandler db;
    private NodeManager mNodeManager;
    private static String TAG = "SourceDestUpdater";
    private SessionManager mSessionManager;
    private NetworkConnection mNetworkConnection;
    private PowerManager.WakeLock mWakeLock;


    public SourceDestUpdater(){
    }

    @Override
        public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"Service oncreate");

    }

    @Override
    public void handleNewLocation(Location location) {
//        Log.i(TAG,"Location received");
        ReverseGeocoderTasker task = new ReverseGeocoderTasker(this, location);
        task.execute();
    }

    @Override
    public void handlegeolocation(String locality_name,Location location) {
        location_frequency_updater(locality_name,location);
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

    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
//        stopService(new Intent(this, SourceDestUpdater.class));

        Log.i(TAG,"Wakelock released");
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

}//Service class....
