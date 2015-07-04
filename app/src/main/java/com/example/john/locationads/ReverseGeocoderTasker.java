package com.example.john.locationads;


import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

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

public class ReverseGeocoderTasker extends AsyncTask<Void,Void,Void>{
    private static String TAG = "ReverseGeocoder";
    private String jsonstring = " ";
    public JSONArray returned_array;
    private Location mlocation;
    private String locality_name;


    interface GeolocationCallBack {
        void handlegeolocation(String locality_name,Location location);
    }

    GeolocationCallBack mGeolocationCallBack;


    public ReverseGeocoderTasker(GeolocationCallBack mGeolocationCallBack,Location mlocation){
       this.mGeolocationCallBack = mGeolocationCallBack;
       this.mlocation = mlocation;
    }

    @Override
    protected Void doInBackground(Void... params) {
        {
            String data_to_send = String.valueOf(mlocation.getLatitude()) + "," + String.valueOf(mlocation.getLongitude());
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpget = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng="+data_to_send);
            Log.i(TAG, "https://maps.googleapis.com/maps/api/geocode/json?latlng="+data_to_send);

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
                returned_array = jObject.getJSONArray("results");

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

    @Override
    protected void onPostExecute(Void aVoid) {
            // interface callback ........................
        try {
//            Log.i(TAG,returned_array.getJSONObject(0).getString("formatted_address"));
            locality_name = returned_array.getJSONObject(0).getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGeolocationCallBack.handlegeolocation(locality_name,mlocation);
    }

}
