package com.lokico.PSWind;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.lokico.PSWind.WindSensorsOverlay.PopupPanel;
import com.google.android.maps.MyLocationOverlay;

public class Omnimap extends MapActivity {
    private MapView map = null;
    private MyLocationOverlay locationOverlay = null;
    private long lastTouchTime = -1;
    private int overlayRetries = 0;
    public PopupPanel panel;
    public WindSensorsOverlay windSensorsOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omnimap);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);

        /* Needed for the compass */
        locationOverlay = new MyLocationOverlay(this, map);
        locationOverlay.runOnFirstFix(centerAroundFix);
        map.getOverlays().add(locationOverlay);
    }

    private Runnable centerAroundFix = new Runnable() {
        public void run() {
            map.getController().animateTo(locationOverlay.getMyLocation());
            map.getController().setZoom(11);
        }
    };
    
    public int getOverlayRetries() {
        return overlayRetries;
    }

    public void setOverlayRetries(int retries) {
        overlayRetries = retries;
    }

    @Override
    public void onResume() {
        super.onResume();

        locationOverlay.enableMyLocation();
        
        if(panel != null) {
            panel.hide();
        }
        
        queueUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();

        locationOverlay.disableMyLocation();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                queueUpdate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void queueUpdate(){
        Handler myHandler = new Handler();
        // Delay loading the overlay by 1 ms to allow the map to display immediately.
        myHandler.postDelayed(mMyRunnable, 100);
    }
    
    //Here's a runnable/handler combo
    private Runnable mMyRunnable = new Runnable() {
        public void run() {
            displayWind();
        }
    };

    private void displayWind() {
        if (windSensorsOverlay != null) {
            map.getOverlays().remove(windSensorsOverlay);
        }
        
        /* Add the Wind Sensors overlay to our map */
        new LoadMapItems(Omnimap.this, map).execute((Object)null);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        return true;
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return (false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_S) {
            map.setSatellite(!map.isSatellite());
            return (true);
        } else if (keyCode == KeyEvent.KEYCODE_Z) {
            map.displayZoomControls(true);
            return (true);
        }

        return (super.onKeyDown(keyCode, event));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            long thisTime = System.currentTimeMillis();
            if (thisTime - lastTouchTime < 250) {
                // Double tap
                lastTouchTime = -1;
                map.getController().zoomInFixing((int) ev.getX(),
                        (int) ev.getY());
            } else {
                // Too slow
                lastTouchTime = thisTime;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
