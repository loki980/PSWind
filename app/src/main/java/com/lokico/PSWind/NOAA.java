package com.lokico.PSWind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NOAA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noa);
    }

    static boolean isActivityImplemented() {
        return false;
    }
}
