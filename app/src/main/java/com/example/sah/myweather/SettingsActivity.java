package com.example.sah.myweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sah.myweather.DB.DBHelper;
import com.example.sah.myweather.DB.DBIcon;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences mSettings;
    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_UNITS = "units_format";
    public static final String APP_PREF_CITY = "cityname";
    public static final String APP_PREF_DB = "database";
    public static final String APP_PREF_LANG = "language";
    Button btnAdd, btnClear;
    EditText editText;
    String units, lang;
    DBHelper dbHelper;
    DBIcon dbIcon;
    RadioButton rbKel, rbCel, rbFar;
    RadioGroup rg;
    ImageButton ibRu, ibEn, ibSearch;
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



        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        rbFar = (RadioButton)findViewById(R.id.rb_far);
        rbKel = (RadioButton)findViewById(R.id.rb_kel);
        rbCel = (RadioButton)findViewById(R.id.rb_cel);


        units = mSettings.getString(APP_PREF_UNITS, "");
        if (units.equals("&units=imperial")){
            rbFar.setChecked(true);
        }
        else if (units.equals("&units=metric")){
            rbCel.setChecked(true);
        }
        else {
            rbKel.setChecked(true);
        }

        editText = (EditText) findViewById(R.id.cityName);
        rg = (RadioGroup)findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = mSettings.edit();
                switch (checkedId) {
                    case -1:
                        Toast.makeText(getApplicationContext(), "Ничего не выбрано",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_kel:
                        Toast.makeText(getApplicationContext(), R.string.toast_l, Toast.LENGTH_SHORT).show();
                        editor.putString(APP_PREF_UNITS, "");
                        editor.apply();

                        break;
                    case R.id.rb_cel:
                        Toast.makeText(getApplicationContext(), R.string.toast_l, Toast.LENGTH_SHORT).show();
                        editor.putString(APP_PREF_UNITS, "&units=metric");
                        editor.apply();
                        break;
                    case R.id.rb_far:
                        Toast.makeText(getApplicationContext(), R.string.toast_l, Toast.LENGTH_SHORT).show();
                        editor.putString(APP_PREF_UNITS, "&units=imperial");
                        editor.apply();
                        break;

                    default:
                        break;
                }
            }
        });

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
ibSearch.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (editText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Text field is empty", Toast.LENGTH_SHORT).show();
        }

        else {
            if (!isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREF_CITY, editText.getText().toString());
                editor.apply();
                new MyTask(getApplicationContext()).execute();
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

    public void changeLanguage(String lang) {
        Locale myLocale;
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        if ((mSettings.getString(APP_PREF_CITY, "")!=null)) {
            Toast.makeText(getApplicationContext(), R.string.toast_wait, Toast.LENGTH_SHORT).show();
            new MyTask(getApplicationContext()).execute();

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }
        else Toast.makeText(this, R.string.toast_lang, Toast.LENGTH_SHORT).show();

    }

}
