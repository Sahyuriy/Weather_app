package com.example.sah.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sah.myweather.db.DBHelper;
import com.example.sah.myweather.db.DBIcon;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_UNITS = "units_format";
    public static final String APP_PREF_CITY = "cityname";
    public static final String APP_PREF_DB = "database";
    public static final String APP_PREF_LANG = "language";

    private SharedPreferences mSettings;
    private Button btnClear;
    private EditText editText;
    private String units;
    private DBHelper dbHelper;
    private DBIcon dbIcon;
    private Button btnKel, btnCel, btnFar;
    private ImageButton ibRu, ibEn, ibSearch;
    private SharedPreferences.Editor editor;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        dbHelper = new DBHelper(this);
        dbIcon = new DBIcon(this);

        mSettings = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        editor = mSettings.edit();

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        btnKel = (Button) findViewById(R.id.btn_kel);
        btnCel = (Button) findViewById(R.id.btn_cel);
        btnFar = (Button) findViewById(R.id.btn_far);

        units = mSettings.getString(APP_PREF_UNITS, "");

        editText = (EditText) findViewById(R.id.cityName);

        ibRu = (ImageButton) findViewById(R.id.ib_ru);
        ibEn = (ImageButton) findViewById(R.id.ib_en);
        ibSearch = (ImageButton) findViewById(R.id.ib_search);


        ibRu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREF_LANG, "&lang=ru");
                editor.apply();
                changeLanguage("ru");
            }
        });

        ibEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREF_LANG, "&lang=en");
                editor.apply();
                changeLanguage("en");
            }
        });


        btnKel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(APP_PREF_UNITS, "");
                editor.apply();
                changeUnits();
            }
        });

        btnCel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(APP_PREF_UNITS, "&units=metric");
                editor.apply();
                changeUnits();
            }
        });
        btnFar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(APP_PREF_UNITS, "&units=imperial");
                editor.apply();
                changeUnits();
            }
        });


        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Text field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isNetworkAvailable()) {
                        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString(APP_PREF_CITY, editText.getText().toString());
                        editor.apply();
                        new MyTask(SettingsActivity.this).execute();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);

                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteDatabase iconbase = dbIcon.getWritableDatabase();

        switch (v.getId()) {
            case R.id.btnClear:
                database.delete(DBHelper.TABLE_WEATHER, null, null);
                iconbase.delete(DBIcon.TABLE_ICON, null, null);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREF_DB, "dbDeleted");
                editor.apply();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                break;
        }
        dbHelper.close();
        dbIcon.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void changeLanguage(String lang) {
        Locale myLocale;
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        new MyTask(SettingsActivity.this).execute();

        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    private void changeUnits() {
        new MyTask(SettingsActivity.this).execute();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }

}
