package com.lokico.PSWind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Launcher extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        Button next = (Button) findViewById(R.id.ButtonOmni);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Omnimap.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        next = (Button) findViewById(R.id.ButtonTJ_Jetty);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TJ_Seattle.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        next = (Button) findViewById(R.id.ButtonTJ_Locust);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TJ_NorthSound.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        next = (Button) findViewById(R.id.Button_NOAA);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), NOAA.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
    }
}
