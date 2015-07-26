package com.example.john.locationads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.net.HttpURLConnection;
import java.net.URL;

public class AdTasker extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "AdTasker";
    private double currentLatitude;
    private double currentLongitude;

    public AdTasker(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

        String jsonstring = " ";
        public JSONArray returned_locations;
        @Override
        protected Void doInBackground(Void... params) {
            if(GlobalVar.getUserEmail()!=null && GlobalVar.getUserToken()!=null) {
                String data_to_send = "lat=" + String.valueOf(currentLatitude) + "&lon=" + String.valueOf(currentLongitude);
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
            }
            else
                return null;
        }

    @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
        Map_Fragment.dataFromAsyncTask = returned_locations;
    }

}
