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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        if ((intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON"))) {

//            Intent i = new Intent(context, NotificationService.class);
//            PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

            if (sp.getBoolean("notification_switch",true)){
                context.startService(new Intent(context,NotificationService.class));
                Toast.makeText(context, "Notification Service started", Toast.LENGTH_LONG).show();
            }
        }
    }

}
