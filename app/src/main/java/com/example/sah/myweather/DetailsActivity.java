package com.example.sah.myweather;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class DetailsActivity extends AppCompatActivity {

    private boolean isTablet;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        String string = getIntent().getStringExtra("ITEM_POSITION");
        position = Integer.valueOf(string);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_det, new DetailsFragment(position))
                .commit();

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        DetailsFragment fragment = new DetailsFragment(position);
//        fragmentTransaction.replace(R.id.fr_det, fragment);
//        fragmentTransaction.commit();


    }

}

