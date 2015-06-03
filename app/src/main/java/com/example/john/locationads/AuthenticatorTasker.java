package com.example.john.locationads;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
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
    private String email,password,username;
    private Dialog login;
    private ProgressDialog dialog;
    private Context context;
    private MainActivity activity;

    public AuthenticatorTasker(String email, String password, String username, Dialog login, Context context, MainActivity activity){
        this.email = email;
        this.password = password;
        this.username = username;
        this.login = login;
        this.activity = activity;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute(){
//        this.dialog.show(activity, "Registering", "Please wait...");
    }

    @Override
    protected Void doInBackground(Void... params) {
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost("http://stormy-brook-6865.herokuapp.com/users.json");
        Log.i(TAG, "http://stormy-brook-6865.herokuapp.com/users.json");

        JSONObject user_params = new JSONObject();
        try {
            user_params.put("email", email);
            user_params.put("password", password);
            user_params.put("username", username);
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
                JSONObject userDetails = jObject.getJSONObject("data");

                Log.i(TAG,userDetails.toString());

                get_auth_token(userDetails);


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
//        if (this.dialog.isShowing())
//            this.dialog.dismiss();
//        this.dialog.setCancelable(true);

        Toast.makeText(activity, "Register Successful", Toast.LENGTH_SHORT).show();
        this.login.dismiss();
    }

    protected void get_auth_token(JSONObject userdetails) throws JSONException {
        GlobalVar.setUserToken(userdetails.getString("authentication_token"));
        GlobalVar.setUserName(userdetails.getString("username"));
        GlobalVar.setUserEmail(userdetails.getString("email"));

        SharedPreferences settings_sp = context.getSharedPreferences(GlobalVar.getSharedPreferenceName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings_sp.edit();
        editor.putString("USERNAME", GlobalVar.getUserName());
        editor.putString("EMAIL", GlobalVar.getUserEmail());
        editor.putString("AUTH_TOKEN", GlobalVar.getUserToken());
        editor.commit();

        Log.i(TAG, GlobalVar.getUserToken());
    }

}
