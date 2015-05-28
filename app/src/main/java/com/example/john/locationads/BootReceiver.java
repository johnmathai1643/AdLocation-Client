package com.example.john.locationads;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class BootReceiver extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {
        // in our case intent will always be BOOT_COMPLETED, so we can just set
        // the alarm
        // Note that a BroadcastReceiver is *NOT* a Context. Thus, we can't use
        // "this" whenever we need to pass a reference to the current context.
        // Thankfully, Android will supply a valid Context as the first parameter
        if ((intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON"))) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Intent i = new Intent(context, NotificationService.class);
            PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
//            startService(new Intent(this,NotificationService.class));
            Toast.makeText(context, "Broadcast started", Toast.LENGTH_LONG).show();
        }
    }
}
