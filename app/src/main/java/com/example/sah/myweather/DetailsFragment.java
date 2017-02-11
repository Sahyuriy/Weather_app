package com.example.sah.myweather;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sah.myweather.db.DBHelper;
import com.example.sah.myweather.db.DBIcon;

import java.util.ArrayList;


public class DetailsFragment extends Fragment {


    ArrayList<String> item = new ArrayList<>();
    RecyclerView rvDetails;
    DBHelper dbHelp;
    DBIcon dbIcon;
    int position;
    String[] weatherList = new String[6];
    String icon;
    private boolean isTablet;


    public DetailsFragment(int pos) {
        position = pos;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dbHelp = new DBHelper(getContext());
        dbIcon = new DBIcon(getContext());
        //String string = getActivity().getIntent().getStringExtra("ITEM_POSITION");
        //position = Integer.valueOf(string);
        details();


        rvDetails = (RecyclerView) view.findViewById(R.id.rvDetails);
        DetailsAdapter adapter = new DetailsAdapter(weatherList, icon);
        rvDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDetails.setAdapter(adapter);
        adapter.setOnItemClickListener(new DetailsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }
        });


        dbIcon.close();
        dbHelp.close();
    }


    public void details() {
        SQLiteDatabase database = dbHelp.getReadableDatabase();
        SQLiteDatabase iconbase = dbIcon.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_WEATHER, null, null, null, null, null, null);
        Cursor iconCursor = iconbase.query(DBIcon.TABLE_ICON, null, null, null, null, null, null);
        cursor.moveToPosition(position);
        iconCursor.moveToPosition(position);

        weatherList[0] = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TIME));
        weatherList[1] = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TEMPERATURE));
        weatherList[2] = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DESCR));
        weatherList[3] = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_HUMIDITY));
        weatherList[4] = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PRESSURE));
        weatherList[5] = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_WIND_SPEED));
        icon = iconCursor.getString(iconCursor.getColumnIndex(DBIcon.KEY_ICON));

        iconCursor.close();
        cursor.close();
    }

}
