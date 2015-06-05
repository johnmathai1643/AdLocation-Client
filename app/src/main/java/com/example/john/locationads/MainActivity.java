package com.example.john.locationads;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String[] NavArray =  { "Current Location", "Ad Location", "Settings", "Register", "Exit" };
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private SessionManager mSessionManager;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.content_frame) != null) {
            if (savedInstanceState != null) {
                return;
            }
            mSessionManager = new SessionManager(getApplicationContext());
            mSessionManager.get_logged();
            mSessionManager.get_registered();
            mSessionManager.set_session();

            create_fragments(new Map_Fragment(100));
 //         startService(new Intent(this,NotificationService.class));

            /** navigational drawer **/
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mActivityTitle = getTitle().toString();
            user_status();

//            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NavArray);
//            mDrawerList.setAdapter(mAdapter);

            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.drawer_open, R.string.drawer_close) {

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle("Navigation!");
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(mActivityTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /** navigation drawer listener swaps fragments in the main content view */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void user_status(){
        if(GlobalVar.getLoggedIn() && GlobalVar.getRegister())
            NavArray = new String[]{"Current Location", "Ad Location", "Settings", "Logout"};
        else if (GlobalVar.getRegister())
            NavArray = new String[]{"Current Location", "Ad Location", "Settings", "Login"};
        else
            NavArray = new String[]{"Current Location", "Ad Location", "Settings", "Register"};

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NavArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void selectItem(int position) {

        switch (position) {
            case 0:
                create_fragments(new Map_Fragment(0));
                break;
            case 1:
                create_fragments(new Map_Fragment(1));
                break;
            case 2:
                create_fragments(new SettingsFragment(this));
                break;
            case 3:
                if(GlobalVar.getLoggedIn() && GlobalVar.getRegister())
                    create_logout_dialog();
                else if (GlobalVar.getRegister())
                    create_login_dialog();
                else
                    create_register_dialog();
                break;
           default:
               break;
           }

        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        setTitle(NavArray[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    private void create_logout_dialog(){

    }

    private void create_login_dialog(){

        final Dialog login = new Dialog(this);

        login.setContentView(R.layout.login_dialog);
        login.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

        Button btnLogin = (Button) login.findViewById(R.id.btnSingIn);
        final EditText txtPassword = (EditText)login.findViewById(R.id.etUserName);
        final EditText txtUsername = (EditText)login.findViewById(R.id.etPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUsername.getText().toString().trim().length() > 0 && txtPassword.getText().toString().trim().length() > 0)
                {
                    String password = txtPassword.getText().toString();
                    String username = txtUsername.getText().toString();

                    AsyncTask<Void, Void, Void> AuthenticatorTasker_object;
                    AuthenticatorTasker_object = new AuthenticatorTasker(password,username,login,getApplicationContext(),MainActivity.this).execute();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Enter Username and Password", Toast.LENGTH_LONG).show();
                }
            }
        });
        login.show();
    }

    private void create_register_dialog() {

        final Dialog register = new Dialog(this);

        register.setContentView(R.layout.register_dialog);
        register.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

        Button btnLogin = (Button) register.findViewById(R.id.btnSingIn);
//        Button btnCancel = (Button) login.findViewById(R.id.btnCancel);
        final EditText txtEmail = (EditText)register.findViewById(R.id.etEmail);
        final EditText txtPassword = (EditText)register.findViewById(R.id.etPass);
        final EditText txtUsername = (EditText)register.findViewById(R.id.etUserName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUsername.getText().toString().trim().length() > 0 && txtEmail.getText().toString().trim().length() > 0 && txtPassword.getText().toString().trim().length() > 0)
                {
                    String email = txtEmail.getText().toString();
                    String password = txtPassword.getText().toString();
                    String username = txtUsername.getText().toString();

                    AsyncTask<Void, Void, Void> AuthenticatorTasker_object;
                    AuthenticatorTasker_object = new AuthenticatorTasker(email,password,username,register,getApplicationContext(),MainActivity.this).execute();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Enter Email and Password", Toast.LENGTH_LONG).show();
                }
            }
        });

//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                login.dismiss();
//            }
//        });

        register.show();

    }

    private void create_fragments(Fragment fragment){
        Bundle data = new Bundle();
        fragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        fragmentManager.beginTransaction().addToBackStack(null);
    }

    /** for sync on navigation drawer actions and animations **/
    private void setupDrawer() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/** action menu function not implemented yet **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}