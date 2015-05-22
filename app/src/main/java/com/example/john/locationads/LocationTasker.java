package com.example.john.locationads;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by john on 5/17/15.
 */
public class LocationTasker extends AsyncTask<Void,Void,Void> {

    public LocationTasker(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

        String jsonstring = " ";
        String result = "";

        private double currentLatitude;
        private double currentLongitude;

        @Override
        protected Void doInBackground(Void... params) {
            String data_to_send = String.valueOf(currentLatitude)+"+"+String.valueOf(currentLongitude);
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost("http://headers.jsontest.com/");
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
//                    Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();
                }
                jsonstring = sb.toString();
                JSONObject jObject = new JSONObject(jsonstring);

                JSONArray jArray = jObject.getJSONArray("adlocations");
                outputlocations(jArray);

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
            super.onPostExecute(aVoid);
        }

        protected void outputlocations(JSONArray jsonArray){

            try {
                for (int i = 0; i<jsonArray.length();i++) {
                    JSONObject adlocations = jsonArray.getJSONObject(i);
                }
//                Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

}
