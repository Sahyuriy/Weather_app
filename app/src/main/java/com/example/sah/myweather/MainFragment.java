package com.example.sah.myweather;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sah.myweather.DB.DBHelper;
import com.example.sah.myweather.DB.DBIcon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainFragment extends Fragment {


    RecyclerView recyclerView;
    DBHelper dbHelper;
    DBIcon dbIcon;
    int ke = 0;
    String[] recList = new String[6];
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> iconItems = new ArrayList<>();

    TextView tvTime;
    ImageView iv_icon;
    TextView tvTemp;
    TextView tvDescr;
    TextView tvCityName;
    private boolean isTablet;
    SharedPreferences mSettings;
    public static final String APP_PREF = "mysettings";
    public static final String APP_PREF_UNITS = "units_format";
    public static final String APP_PREF_CITY = "cityname";
    public static final String APP_PREF_DB = "database";
    String dbCreated;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        recyclerView = (RecyclerView) view.findViewById(R.id.rvItem);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        iv_icon = (ImageView) view.findViewById(R.id.iv_image);
        tvTemp = (TextView) view.findViewById(R.id.tvTemp);
        tvDescr = (TextView) view.findViewById(R.id.tvDescr);
        tvCityName = (TextView) view.findViewById(R.id.tvCityName);




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        mSettings = getActivity().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        dbCreated = mSettings.getString(APP_PREF_DB, "");
        dbHelper = new DBHelper(getContext());
        dbIcon = new DBIcon(getContext());
        //getContent();
        if (dbCreated.equals("dbCreated")){

            getContent();

        }
        else {
            Toast.makeText(getContext(), "Database is empty", Toast.LENGTH_SHORT).show();
        }



    }

    public void getContent() {

        ke=0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteDatabase iconbase = dbIcon.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_WEATHER, null, null, null, null, null, null);
        Cursor cursorIcon = iconbase.query(DBIcon.TABLE_ICON, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int time = cursor.getColumnIndex(DBHelper.KEY_TIME);

            do {
                ke++;
                items.add(String.valueOf(cursor.getString(time)));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");

        cursorIcon.moveToFirst();
        for (int i = 0; i < ke; i++) {
            iconItems.add(String.valueOf(cursorIcon.getString(cursorIcon.getColumnIndex(DBIcon.KEY_ICON))));
            cursorIcon.moveToNext();
        }

        cursor.moveToFirst();
        recList[0] = String.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TIME)));
        recList[1] = String.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TEMPERATURE)));
        recList[2] = String.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DESCR)));
        recList[3] = String.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_HUMIDITY)));
        recList[4] = String.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PRESSURE)));
        recList[5] = String.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_WIND_SPEED)));

        tvTime.setText(recList[0]);
        tvTemp.setText(recList[1]);
        tvDescr.setText(recList[2]);
        tvCityName.setText(mSettings.getString(APP_PREF_CITY, ""));
        String iconName = (iconItems.get(0));
        Picasso.with(getContext()).load("http://openweathermap.org/img/w/" + iconName + ".png").into(iv_icon);
        cursorIcon.close();
        cursor.close();


        MyAdapter myAdapter = new MyAdapter(items, iconItems);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), ke));
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

                if (isTablet){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    DetailsFragment fragment = new DetailsFragment(position);
                    fragmentTransaction.replace(R.id.fragm_det, fragment);
                    fragmentTransaction.commit();

                }
                else {
                    String str = String.valueOf(position);
                    Intent intent = new Intent(getContext(),DetailsActivity.class);
                    intent.putExtra("ITEM_POSITION", str);
                    startActivity(intent);
                }



            }
        });
    }



}
