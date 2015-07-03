package com.example.john.locationads;


import android.content.Context;
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

public class DirectionManager extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "DirectionManager";
    private Context context;

    public DirectionManager(Context context){
        this.context = context;
    }

    String jsonstring = " ";
    public JSONArray returned_array;

    @Override
    protected Void doInBackground(Void... params) {
        {
//            String data_to_send = "lat=" + String.valueOf(currentLatitude) + "&lon=" + String.valueOf(currentLongitude);
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpget = new HttpGet("http://maps.googleapis.com/maps/api/directions/json?origin=40.64974840,-73.94998180&destination=40.65084299999999,-73.9495750&sensor=false&mode=transit");
            Log.i(TAG, "http://maps.googleapis.com/maps/api/directions/json?origin=40.64974840,-73.94998180&destination=40.65084299999999,-73.9495750&sensor=false&mode=transit");

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
                returned_array = jObject.getJSONArray("routes");

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
//            super.onPostExecute(aVoid);
        JSONObject jObject = null;
        try {
            jObject = returned_array.getJSONObject(0);
            JSONArray jarray_legs = jObject.getJSONArray("legs");
            JSONObject jobject_legs = jarray_legs.getJSONObject(0);
            JSONArray jarray_steps = jobject_legs.getJSONArray("steps");
            JSONObject jobject_steps = jarray_steps.getJSONObject(0);
            JSONArray jarray_steps_steps = jobject_steps.getJSONArray("steps");

            DatabaseHandler db = new DatabaseHandler(context);

            for (int i=0; i < jarray_steps_steps.length(); i++)
            {
                try {
                    JSONObject oneObject = jarray_steps_steps.getJSONObject(i);
                    JSONObject start_location_Object = oneObject.getJSONObject("start_location");
                    JSONObject end_location_Object = oneObject.getJSONObject("end_location");

                    Log.i(TAG, String.valueOf(start_location_Object.getDouble("lat")));
                    Log.i(TAG,start_location_Object.getString("lng"));
                    Log.i(TAG,end_location_Object.getString("lat"));
                    Log.i(TAG,end_location_Object.getString("lng"));

                    /**
                     * CRUD Operations
                     * */
                    // Inserting Contacts
                    Log.d("Insert: ", "Inserting ..");
                    db.addFreq(new FreqManager(1,start_location_Object.getDouble("lat"),start_location_Object.getDouble("lng"),end_location_Object.getDouble("lat"),end_location_Object.getDouble("lng"),1));

                } catch (JSONException e) {
                    // Oops
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
