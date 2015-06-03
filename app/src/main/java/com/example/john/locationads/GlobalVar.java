package com.example.john.locationads;

import android.app.Application;

public class GlobalVar extends Application {

    static private String name;
    static private String email;
    static private String auth_token;
    private static final String SHAREDPREF_SETTINGS = "Settings";


    static public String getSharedPreferenceName() { return SHAREDPREF_SETTINGS; }

    static public String getUserName() {
        return GlobalVar.name;
    }

    static public void setUserName(String name) {
        GlobalVar.name = name;
    }

    static public String getUserEmail() {
        return GlobalVar.email;
    }

    static public void setUserEmail(String email) {
       GlobalVar.email = email;
    }

    static public String getUserToken() {
        return GlobalVar.auth_token;
    }

    static public void setUserToken(String g_auth_token) {
        GlobalVar.auth_token = g_auth_token;
    }

}
