package com.example.john.locationads;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.MapFragment;

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

public class LocationTasker extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "data";
    private double currentLatitude;
    private double currentLongitude;

    public LocationTasker(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }
        String jsonstring = " ";
        public JSONArray returned_locations;
        @Override
        protected Void doInBackground(Void... params) {
            String data_to_send = "lat="+String.valueOf(currentLatitude)+"&lon="+String.valueOf(currentLongitude);
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httppost = new HttpGet("http://stormy-brook-6865.herokuapp.com/api/ads_manager?"+data_to_send);
            Log.i(TAG,"http://http://stormy-brook-6865.herokuapp.com/api/ads_manager?"+data_to_send);
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

    @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
        Map_Fragment.dataFromAsyncTask = returned_locations;
    }

}
