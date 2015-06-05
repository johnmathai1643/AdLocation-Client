package com.example.john.locationads;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private Context context;

    public SessionManager(Context context){
        this.context = context;
    }

    public void get_logged(){
        SharedPreferences settings_sp = context.getSharedPreferences(GlobalVar.getSharedPreferenceName(), Context.MODE_PRIVATE);
        Boolean LOGGED_IN = settings_sp.getBoolean("LOGGED_IN",false);
        GlobalVar.setLoggedIn(LOGGED_IN);
    }

    public void get_registered(){
        SharedPreferences settings_sp = context.getSharedPreferences(GlobalVar.getSharedPreferenceName(), Context.MODE_PRIVATE);
        Boolean REGISTER = settings_sp.getBoolean("REGISTER",false);
        GlobalVar.setRegister(REGISTER);
    }

    public void set_session(){
        SharedPreferences settings_sp = context.getSharedPreferences(GlobalVar.getSharedPreferenceName(), Context.MODE_PRIVATE);
        GlobalVar.setUserToken(settings_sp.getString("AUTH_TOKEN", null));
        GlobalVar.setUserName(settings_sp.getString("USERNAME", null));
        GlobalVar.setUserEmail(settings_sp.getString("EMAIL", null));
    }

}
