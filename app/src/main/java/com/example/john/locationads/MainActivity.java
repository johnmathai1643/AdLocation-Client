package com.example.john.locationads;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    static LatLng CurLocation;

    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String[] osArray =  { "Current Location", "Ad Location", "Recent", "Settings", "Exit" };
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    public static JSONArray dataFromAsyncTask;
    private ProgressDialog dialog;
    private Location location_current;
    private Handler h;

    /** map fragment **/
    private GoogleMap map;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        /** current location  **/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        /** navigationa drawer **/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
/**  current location googleclientapi functions **/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
            location_current = location;
        };

    }
        private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        CurLocation = new LatLng(currentLatitude, currentLongitude);
        Marker MyLocation = map.addMarker(new MarkerOptions().position(CurLocation).title("Current Location").snippet("This is your location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurLocation, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        AsyncTask<Void, Void, Void> LocationTasker_object;
        LocationTasker_object = new LocationTasker(location).execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /** navigation drawer listener swaps fragments in the main content view */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

//        Fragment fragment = null;
        switch (position) {
            case 0:
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location == null)
                  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                else
                  handleNewLocation(location);
                break;
            case 1:
                if (dataFromAsyncTask==null){
                    dialog = ProgressDialog.show(MainActivity.this,"Get Locations", "Please wait...");
                    h = new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                          super.handleMessage(msg);
                            dialog.dismiss();
                            output_ad_locations(dataFromAsyncTask);
                        }
                    };

                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                get_ad_location_sync();
                                h.sendEmptyMessage(0);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                    } });
                    th.start();
                 }
                else
                  output_ad_locations(dataFromAsyncTask);
                break;
           default:
               break;
           }

//        if (fragment != null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
//            setTitle(osArray[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
//            }
//        else
//           Log.e("MainActivity", "Error in creating fragment");
    }

    protected void output_ad_locations(JSONArray jsonArray){
        try {
            for (int i = 0; i<jsonArray.length();i++) {
                JSONObject adlocation = jsonArray.getJSONObject(i);
                map.addMarker(new MarkerOptions()
                                .title(adlocation.getString("name"))
                                .snippet(adlocation.getString("snippet"))
                                .position(new LatLng(Double.parseDouble(adlocation.getString("lat")), Double.parseDouble(adlocation.getString("lon"))))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
            }
            dataFromAsyncTask = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject get_ad_location_sync(){
        String jsonstring;
        double currentLatitude = location_current.getLatitude();
        double currentLongitude = location_current.getLongitude();

        String data_to_send = "lat="+String.valueOf(currentLatitude)+"&lon="+String.valueOf(currentLongitude);
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        if (dataFromAsyncTask == null)
        {   HttpGet httpget = new HttpGet("http://stark-lake-4080.herokuapp.com/api/ads_manager?"+data_to_send);
            Log.i(TAG,"http://stark-lake-4080.herokuapp.com/api/ads_manager?"+data_to_send);
            httpget.setHeader("Content-type", "application/json");
            InputStream inputstream = null;
            try {
                HttpResponse response = httpclient.execute(httpget);
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
                dataFromAsyncTask = jArray;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** for sync on navigation drawer actions and animations **/
    private void setupDrawer() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/** action menu function not implemented yet **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



}