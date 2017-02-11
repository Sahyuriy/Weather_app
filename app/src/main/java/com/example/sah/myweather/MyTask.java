package com.example.sah.myweather;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.example.sah.myweather.db.DBHelper;
import com.example.sah.myweather.db.DBIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MyTask extends AsyncTask<Void, Void, String> {

    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_UNITS = "units_format";
    public static final String APP_PREF_CITY = "cityname";
    public static final String APP_PREF_DB = "database";
    public static final String APP_PREF_LANG = "language";
    private String icon;
    private String units, city, lang;
    private int ke = 0;
    private DBHelper dbHelper;
    private DBIcon dbIcon;
    private ProgressDialog pd;
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String resultJson = "";
    private JSONObject dataJsonObj = null;
    private JSONObject dataWeatherJsonObj = null;
    private JSONObject dataTemperatureJsonObj = null;
    private JSONObject dataWindJsonObj = null;
    private JSONArray weatherJsonObj = null;
    private String data = "";
    private JSONObject dataCity = null;
    private JSONArray listJsonArray = null;
    private JSONObject tempJson = null;
    private String pressure = "";
    private String humidity = "";
    private String wind_speed = "";
    private String description = "";
    private String dt_txt = "";
    private String jsonWeather_temp = "";
    private String jsonCityName = "";
    private String dataMain = "";
    private String[] weatherList = new String[6];
    private SharedPreferences mSettings;
    private Context context;


    public MyTask(AppCompatActivity activity) {
        context = activity;
        pd = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setMessage(context.getResources().getString(R.string.toast_wait));
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            dbHelper = new DBHelper(context);
            dbIcon = new DBIcon(context);
            mSettings = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);


            city = mSettings.getString(APP_PREF_CITY, "");

            if (mSettings.getString(APP_PREF_UNITS, "").equals("&units=imperial")) {
                units = "&units=imperial";
            } else if (mSettings.getString(APP_PREF_UNITS, "").equals("&units=metric")) {
                units = "&units=metric";
            } else {
                units = "";
            }
            if (mSettings.getString(APP_PREF_LANG, "").equals("&lang=en")) {
                lang = "&lang=en";
            } else {
                lang = "&lang=ru";
            }


            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=" + city + units + lang + "&appid=9969e65c30d6a4788a27340c1ea4f3f6");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);


        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteDatabase iconbase = dbIcon.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        ContentValues conValues = new ContentValues();

        if (mSettings.getString(APP_PREF_DB, "").equals("dbCreated")) {
            database.delete(DBHelper.TABLE_WEATHER, null, null);
            iconbase.delete(DBIcon.TABLE_ICON, null, null);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREF_DB, "dbDeleted");
            editor.apply();
        }


        try {
            dataJsonObj = new JSONObject(strJson);
            dataCity = dataJsonObj.getJSONObject("city");
            jsonCityName = dataCity.getString("name");
            listJsonArray = dataJsonObj.getJSONArray("list");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonCityName.equals(city)) {

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREF_DB, "dbCreated");
            editor.apply();

            for (int i = 0; i < listJsonArray.length(); i++) {
                getDataFromJSON(strJson, i);
                conValues.put(DBIcon.KEY_ICON, icon);
                iconbase.insert(DBIcon.TABLE_ICON, null, conValues);
            }


            for (int i = 0; i < listJsonArray.length(); i++) {
                getDataFromJSON(strJson, i);
                weatherList[0] = weatherList[0].substring(8, 10) + "/" + weatherList[0].substring(5, 7) + "/" +
                        weatherList[0].substring(0, 4) + "\n" +
                        weatherList[0].substring(11, weatherList[0].length() - 3);
                ;

                contentValues.put(DBHelper.KEY_TIME, weatherList[0]);
                contentValues.put(DBHelper.KEY_TEMPERATURE, weatherList[1]);
                contentValues.put(DBHelper.KEY_DESCR, weatherList[2]);
                contentValues.put(DBHelper.KEY_HUMIDITY, weatherList[3]);
                contentValues.put(DBHelper.KEY_PRESSURE, weatherList[4]);
                contentValues.put(DBHelper.KEY_WIND_SPEED, weatherList[5]);
                database.insert(DBHelper.TABLE_WEATHER, null, contentValues);
            }
            dbHelper.close();
            dbIcon.close();
        } else {
            Toast.makeText(context, "incorrect city name", Toast.LENGTH_SHORT).show();

        }

        dbHelper.close();
        dbIcon.close();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (pd.isShowing()) {
            pd.dismiss();
            Toast toast = Toast.makeText(context, R.string.toast_done, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }


    }

    private void getDataFromJSON(String str, int value) {
        try {
            dataJsonObj = new JSONObject(str);
            dataCity = dataJsonObj.getJSONObject("city");
            jsonCityName = dataCity.getString("name");
            listJsonArray = dataJsonObj.getJSONArray("list");

            tempJson = listJsonArray.getJSONObject(value);


            dataTemperatureJsonObj = tempJson.getJSONObject("main");
            jsonWeather_temp = dataTemperatureJsonObj.getString("temp");
            humidity = String.valueOf(dataTemperatureJsonObj.getInt("humidity"));
            pressure = String.valueOf(dataTemperatureJsonObj.getInt("pressure"));

            weatherJsonObj = tempJson.getJSONArray("weather");
            data = weatherJsonObj.getString(0);
            dataWeatherJsonObj = new JSONObject(data);
            dataMain = dataWeatherJsonObj.getString("main");
            description = dataWeatherJsonObj.getString("description");
            icon = dataWeatherJsonObj.getString("icon");
            dt_txt = tempJson.getString("dt_txt");

            dataWindJsonObj = tempJson.getJSONObject("wind");
            wind_speed = dataWindJsonObj.getString("speed");

            weatherList[0] = dt_txt;


            String units = mSettings.getString(APP_PREF_UNITS, "");
            if (units == "&units=imperial") {
                weatherList[1] = jsonWeather_temp + " ºF";
            } else if (units == "&units=metric") {
                weatherList[1] = jsonWeather_temp + " ºC";
            } else {
                weatherList[1] = jsonWeather_temp + " ºK";
            }
            weatherList[2] = description;
            weatherList[3] = humidity;
            weatherList[4] = pressure;
            weatherList[5] = wind_speed + " m/s";

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}


