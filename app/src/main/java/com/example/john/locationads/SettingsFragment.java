package com.example.john.locationads;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Field;

import static android.content.Context.*;
import static android.widget.Toast.LENGTH_SHORT;

public class SettingsFragment extends PreferenceFragment {

    private Context context;
    public SettingsFragment(MainActivity mainActivity) {
        context = mainActivity;
    }

    private static final String SHAREDPREF_SETTINGS = "Settings";
    Preference notification_switch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_fragment);

//        notification_switch = (SwitchPreference) getView().findViewById(R.id.notification_switch);
//        notification_switch.setChecked(true);
         notification_switch = new SwitchPreference(context);

        PreferenceManager preferenceManager = getPreferenceManager();
        if (preferenceManager.getSharedPreferences().getBoolean("notification_switch", true)){
            set_switch_preference(true);
        } else {
            set_switch_preference(false);
        }

        notification_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean switched = ((SwitchPreference) preference).isChecked();
                set_switch_preference(!switched);
                return true;
            }

        });

    } /** create **/

    protected void set_switch_preference(Boolean value){
        SharedPreferences settings_pref = context.getSharedPreferences(SHAREDPREF_SETTINGS, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings_pref.edit();
        editor.putBoolean("notification_switch",value);
        editor.commit();
        notification_switch.setSummary(value == false ? "Disabled" : "Enabled");
        Toast.makeText(context, value.toString(), LENGTH_SHORT).show();
    }

}