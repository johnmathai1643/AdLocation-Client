package com.example.john.locationads;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

public class Map_Fragment extends Fragment implements LocationProvider.LocationCallback {

    public static JSONArray dataFromAsyncTask;
    public static Location LOCATION_CURRENT;
    MapView mapView;
    GoogleMap map;
    public static final String TAG = MainActivity.class.getSimpleName();
    static LatLng CurLocation;
    private ProgressDialog dialog;
    private Handler h;

    int CASE_NUM = 0;

    private LocationProvider mLocationProvider;
    private NetworkConnection mNetworkConnection;
    public Map_Fragment(int i){
         CASE_NUM = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        mLocationProvider = new LocationProvider(getActivity(), this);
        mNetworkConnection = new NetworkConnection(getActivity());

/** start service implement later **/

        choose_map(CASE_NUM);

        return view;
    }

    private void choose_map(int position){

        switch (position) {
            case 0:
                handleNewLocation(LOCATION_CURRENT);
                break;
            case 1:
                if(mNetworkConnection.internet_connection()) {
                    if (dataFromAsyncTask == null) {
                        dialog = ProgressDialog.show(getActivity(), "Getting Locations", "Please wait...");
                        dialog.setCancelable(true);
                        h = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                dialog.dismiss();
                                output_ad_locations(dataFromAsyncTask);
                            }
                        };

                        Thread th = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    get_ad_location_sync();
                                    h.sendEmptyMessage(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        th.start();
                    } else
                        output_ad_locations(dataFromAsyncTask);
                }
                else
                    Toast.makeText(getActivity(), " No data connection found", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    public void onPause(){
        super.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurLocation, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            dataFromAsyncTask = null;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject get_ad_location_sync(){
       if(mNetworkConnection.internet_connection()) {
           String jsonstring;
           double currentLatitude = LOCATION_CURRENT.getLatitude();
           double currentLongitude = LOCATION_CURRENT.getLongitude();

           String data_to_send = "lat=" + String.valueOf(currentLatitude) + "&lon=" + String.valueOf(currentLongitude);
           DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
           if (dataFromAsyncTask == null) {
               HttpGet httpget = new HttpGet("http://stark-lake-4080.herokuapp.com/api/ads_manager?" + data_to_send);
               Log.i(TAG, "http://stark-lake-4080.herokuapp.com/api/ads_manager?" + data_to_send);
               httpget.setHeader("Content-type", "application/json");
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
                   dataFromAsyncTask = jArray;

               } catch (ClientProtocolException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }
       else{
           Toast.makeText(getActivity(), " No data connection found", Toast.LENGTH_LONG).show();
       }

        return null;
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        CurLocation = new LatLng(currentLatitude, currentLongitude);
        Marker MyLocation = map.addMarker(new MarkerOptions().position(CurLocation).title("Current Location").snippet("This is your location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurLocation, 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        if(mNetworkConnection.internet_connection()){
           AsyncTask<Void, Void, Void> LocationTasker_object;
           LocationTasker_object = new LocationTasker(location).execute();}
        else
           Toast.makeText(getActivity(), " No data connection found", Toast.LENGTH_LONG).show();

    }

}