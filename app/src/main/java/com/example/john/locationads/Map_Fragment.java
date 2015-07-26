package com.example.john.locationads;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Map_Fragment extends Fragment implements LocationProvider.LocationCallback,GoogleMap.OnMarkerDragListener {

    public static JSONArray dataFromAsyncTask;
    public static Location LOCATION_CURRENT;
    MapView mapView;
    GoogleMap map;
    public static final String TAG = MainActivity.class.getSimpleName();
    static LatLng CurLocation;
    private ProgressDialog dialog;
    private Handler h;
    boolean markerClicked;
    private ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

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
        map.setOnMarkerDragListener(this);

        mNetworkConnection = new NetworkConnection(getActivity());
        mLocationProvider = new LocationProvider(getActivity(), this);

        /** start service implement later **/
        choose_map(CASE_NUM);
        return view;
    }

    private void choose_map(int position){

        switch (position) {
            case 0:
                mLocationProvider = new LocationProvider(getActivity(), this);
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
       if (jsonArray != null) {
           try {
               for (int i = 0; i < jsonArray.length(); i++) {
                   JSONObject adlocation = jsonArray.getJSONObject(i);
                   map.addMarker(new MarkerOptions()
                                   .title(adlocation.getString("name"))
                                   .snippet(adlocation.getString("snippet"))
                                   .position(new LatLng(Double.parseDouble(adlocation.getString("latitude")), Double.parseDouble(adlocation.getString("longitude"))))
                                   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                   ).setDraggable(true);

                   map.setInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity(),bitmapArray.get(i)));
               }

               map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurLocation, 12));
               map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
               dataFromAsyncTask = null;

           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
        else
           Toast.makeText(getActivity(),"No Ads found near your location",Toast.LENGTH_LONG).show();
    }

    private void getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            bitmapArray.add(myBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject get_ad_location_sync(){
       if(mNetworkConnection.internet_connection()) {
         if(GlobalVar.getUserEmail()!=null && GlobalVar.getUserToken()!=null) {
             String jsonstring;
             double currentLatitude = LOCATION_CURRENT.getLatitude();
             double currentLongitude = LOCATION_CURRENT.getLongitude();

             String data_to_send = "lat=" + String.valueOf(currentLatitude) + "&lon=" + String.valueOf(currentLongitude);
             DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
             if (dataFromAsyncTask == null) {
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
                     dataFromAsyncTask = jArray;

                     jObject = new JSONObject(jsonstring);
                     JSONArray jsonArray = jObject.getJSONArray("adlocation");

                     for (int i = 0; i<jsonArray.length();i++) {
                         JSONObject adlocation = jsonArray.getJSONObject(i);
                         Log.i(TAG,"http://stormy-brook-6865.herokuapp.com/" + adlocation.getString("image"));
                         getBitmapFromURL("http://stormy-brook-6865.herokuapp.com/" + adlocation.getString("image"));
                     }

                 } catch (ClientProtocolException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }

         }
          else
             Toast.makeText(getActivity(), "You are not logged in.", Toast.LENGTH_LONG).show();
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurLocation, 12));
        map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        mLocationProvider.set_location_preference(location);
//
//        if(mNetworkConnection.internet_connection()){
//           AsyncTask<Void, Void, Void> mAdTasker;
//           mAdTasker = new AdTasker(location).execute();}
//        else
//           Toast.makeText(getActivity(), " No data connection found", Toast.LENGTH_LONG).show();

    }

    private void create_fragments(Fragment fragment,Marker ad_marker){
        Bundle bundle = new Bundle();
        bundle.putParcelable("MARKER", new MarkerParcelable(ad_marker));
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(getActivity(),marker.getSnippet().toString(),Toast.LENGTH_LONG).show();
        create_fragments(new Ad_Fragment(),marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }


}