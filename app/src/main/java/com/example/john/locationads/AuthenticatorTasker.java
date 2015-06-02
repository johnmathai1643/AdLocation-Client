package com.example.john.locationads;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class AuthenticatorTasker extends AsyncTask<Void,Void,Void> {

    private static final String TAG = AuthenticatorTasker.class.getName();
    private String jsonstring = " ";

    public AuthenticatorTasker(){

    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected Void doInBackground(Void... params) {
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost("http://stormy-brook-6865.herokuapp.com/users.json");
        Log.i(TAG, "http://stormy-brook-6865.herokuapp.com/users.json");

        // 3. build jsonObject
        JSONObject user_params = new JSONObject();
        String email = "joh1993@gmail.com";
        String password = "123456789";
        try {
            user_params.put("email", email);
            user_params.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject userObject = new JSONObject();
        try {
            userObject.put("user", user_params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String send_json = userObject.toString();

        Log.i(TAG,send_json);

        StringEntity en = null;
        try {
            en = new StringEntity(send_json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httppost.setEntity(en);

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
                JSONObject userDetails = jObject.getJSONObject("user");

                Log.i(TAG,userDetails.toString());


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

}
