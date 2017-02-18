package com.example.sah.myweather;

import android.annotation.TargetApi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sah.myweather.db.DBHelper;
import com.example.sah.myweather.db.DBIcon;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_DB = "database";
    public static final String APP_PREF_LANG = "language";
    private boolean isTablet;
    private DBHelper dbHelper;
    private DBIcon dbIcon;
    private String lang;
    private SharedPreferences mSettings;
    private DrawerLayout drawer;
    private ProgressDialog pd;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(125);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbHelper = new DBHelper(this);
        dbIcon = new DBIcon(this);
        mSettings = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);


        if (mSettings.getString(APP_PREF_DB, "").equals("dbCreated")){

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragm, new MainFragment())
                    .commit();

        }
        else {
            Toast.makeText(this, "Enter your city", Toast.LENGTH_LONG).show();
            Intent in = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(in, 1);
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.upd:
                if (!isNetworkAvailable()) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    MyTask myTask = new MyTask(MainActivity. this);
                    myTask.execute();

//                    while(myTask.getStatus()!= AsyncTask.Status.FINISHED)
//                    while(myTask.getStatus().equals( AsyncTask.Status.RUNNING))
//                    {
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragm, new MainFragment())
                            .commit();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent in = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(in, 1);
        } else if (id == R.id.quit) {
            finishAffinity();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragm, new MainFragment())
                    .commit();

        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            drawer.openDrawer(GravityCompat.START);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}