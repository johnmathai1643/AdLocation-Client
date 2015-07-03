package com.example.john.locationads;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
    private String email,password,username,type;
    private Dialog login;
    private ProgressDialog dialog;
    private Context context;
    private MainActivity activity;
    private String URL;
    private DatabaseHandler db;

    public AuthenticatorTasker(String email, String password, String username, Dialog login, Context context, MainActivity activity){
        this.email = email;
        this.password = password;
        this.username = username;
        this.login = login;
        this.activity = activity;
        this.dialog = new ProgressDialog(context);
        this.URL = "http://stormy-brook-6865.herokuapp.com/users.json";
        this.type = "register";
    }

    public AuthenticatorTasker(String password, String email, Dialog login, Context context, MainActivity activity){
        this.password = password;
        this.email = email;
        this.login = login;
        this.activity = activity;
        this.dialog = new ProgressDialog(context);
        this.URL = "http://stormy-brook-6865.herokuapp.com/users/sign_in";
        this.type = "login";
    }

    public AuthenticatorTasker(Context applicationContext, MainActivity activity) {
        this.activity = activity;
        this.context = applicationContext;
        this.URL = "http://stormy-brook-6865.herokuapp.com/users/sign_out";
        this.type = "logout";
    }

    protected String createJsonString() {
        JSONObject user_params = new JSONObject();

        if (this.type == "register") {
            try {
                user_params.put("username", username);
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
            Log.i(TAG, send_json);
            return send_json;

        }
        else if (this.type == "login"){
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
            Log.i(TAG, send_json);
            return send_json;
        }
        else
            return null;
    }

    @Override
    protected void onPreExecute(){
//        this.dialog.show(activity, "Registering", "Please wait...");
    }

    @Override
    protected Void doInBackground(Void... params) {
        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost(URL);
        Log.i(TAG, URL);

        StringEntity en = null;
        try {
            en = new StringEntity(createJsonString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httppost.setEntity(en);

        if (this.type == "register")
            httppost.setHeader("Content-type", "application/json");
        else if (this.type == "login"){
            httppost.setHeader("Content-type", "application/json");
            httppost.addHeader("Accept", "application/json");
        }

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

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        if (this.dialog.isShowing())
//            this.dialog.dismiss();
//        this.dialog.setCancelable(true);

        try {
            get_auth_token();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.login.dismiss();
    }

    protected void get_auth_token() throws JSONException {

        String decider;

        if (this.type == "register")
            decider = "data";
        else if (this.type == "login")
            decider = "user";
        else
            decider = null;

        if(jsonstring!=null){
             JSONObject jObject = new JSONObject(jsonstring);
             JSONObject userDetails = jObject.getJSONObject(decider);
             Log.i(TAG, userDetails.toString());
             GlobalVar.setUserToken(userDetails.getString("authentication_token"));
             GlobalVar.setUserName(userDetails.getString("username"));
             GlobalVar.setUserEmail(userDetails.getString("email"));

             SharedPreferences settings_sp = activity.getSharedPreferences(GlobalVar.getSharedPreferenceName(), Context.MODE_PRIVATE);
             SharedPreferences.Editor editor = settings_sp.edit();
             editor.putString("USERNAME", GlobalVar.getUserName());
             editor.putString("EMAIL", GlobalVar.getUserEmail());
             editor.putString("AUTH_TOKEN", GlobalVar.getUserToken());
             editor.putBoolean("LOGGED_IN", true);
             editor.putBoolean("REGISTER", true);
             editor.commit();

            GlobalVar.setLoggedIn(true);
            GlobalVar.setRegister(true);
            SessionManager mSessionManager = new SessionManager(activity);
            mSessionManager.set_session();

            Log.i(TAG, GlobalVar.getUserToken());

            if(this.type == "register") {
                Toast.makeText(activity, "Register Successful", Toast.LENGTH_SHORT).show();
//                NodeManager mNodeManager = new NodeManager(Double.parseDouble(settings_sp.getString("CUR_LNG", null)),Double.parseDouble(settings_sp.getString("CUR_LAT", null)),0.00,0.00,1,System.currentTimeMillis(),System.currentTimeMillis());
//                db.addNode(mNodeManager);
            }
                  else if(this.type == "login")
                Toast.makeText(activity, "Logged in Successfully", Toast.LENGTH_LONG).show();
        }else{
            if (this.type == "register")
                Toast.makeText(activity,"Register Failed",Toast.LENGTH_LONG);
            else if (this.type == "login")
                Toast.makeText(activity,"Login Failed",Toast.LENGTH_LONG);
        }
    }

}
