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
import java.util.ArrayList;
import java.util.List;

public class DirectionManager extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "DirectionManager";
    private Context context;
    private DatabaseHandler db;
    private String jsonstring = " ";
    public JSONArray returned_array;
    private NodeManager mNodeManager;
    private List<NodeManager> nodeList = new ArrayList<NodeManager>();

    public DirectionManager(Context context){
        this.context = context;
        db = new DatabaseHandler(context);
        db.onDrop_freqtable();
        Log.i(TAG,"Table dropped");
    }

    @Override
    protected Void doInBackground(Void... params) {

//            db = new DatabaseHandler(context);
            nodeList = db.getHighestFrequency();

            if(nodeList != null && nodeList.size() == 2) {
//                String data_to_send = nodeList.get(0).get_lat() + "," + nodeList.get(0).get_lng() + "&destination=" + nodeList.get(1).get_lat() + "," + nodeList.get(1).get_lng() + "&sensor=false&mode=driving";
                String data_to_send = nodeList.get(0).get_lat() + "," + nodeList.get(0).get_lng() + "&destination=10.762448,78.811313&sensor=false&mode=driving";
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet("http://maps.googleapis.com/maps/api/directions/json?origin="+data_to_send);
                Log.i(TAG, "http://maps.googleapis.com/maps/api/directions/json?origin="+data_to_send);
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
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
        JSONObject jObject = null;
        if(returned_array != null) {
            try {
                jObject = returned_array.getJSONObject(0);
                JSONArray jarray_legs = jObject.getJSONArray("legs");
                JSONObject jobject_legs = jarray_legs.getJSONObject(0);
                JSONArray jarray_steps = jobject_legs.getJSONArray("steps");
//                JSONObject jobject_steps = jarray_steps.getJSONObject(0);
//                JSONArray jarray_steps_steps = jobject_steps.getJSONArray("steps");

                for (int i = 0; i < jarray_steps.length(); i++) {
                    try {
                        JSONObject oneObject = jarray_steps.getJSONObject(i);
                        JSONObject start_location_Object = oneObject.getJSONObject("start_location");
                        JSONObject end_location_Object = oneObject.getJSONObject("end_location");

//                        Log.i(TAG, start_location_Object.getString("lat"));
//                        Log.i(TAG, start_location_Object.getString("lng"));
                        Log.i(TAG, end_location_Object.getString("lat"));
                        Log.i(TAG, end_location_Object.getString("lng"));

                        Log.d("Insert: ", "Inserting ..");
                        db.addFreq(new FreqManager(1, start_location_Object.getDouble("lat"), start_location_Object.getDouble("lng"), end_location_Object.getDouble("lat"), end_location_Object.getDouble("lng"), 1));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
           Log.i(TAG,"There is only one location registered so far");
        }

    }

}
