package com.lokico.PSWind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class Launcher extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
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
        
        Button btn;
        btn = (Button)findViewById(R.id.ButtonOmni);
        btn.getBackground().setAlpha(100);
        btn = (Button)findViewById(R.id.ButtonTJ_Jetty);
        btn.getBackground().setAlpha(100);
        btn = (Button)findViewById(R.id.ButtonTJ_Locust);
        btn.getBackground().setAlpha(100);
        btn = (Button)findViewById(R.id.Button_NOAA);
        btn.getBackground().setAlpha(100);
    }
}
