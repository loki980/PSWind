package com.lokico.PSWind;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.lokico.PSWind.WindSensorsOverlay.PopupPanel;

public class Omnimap extends MapActivity {
    private MapView map = null;
    private MyLocationOverlay locationOverlay = null;
    private long lastTouchTime = -1;
    private int overlayRetries = 0;
    public PopupPanel panel;
    private LoadMapItems LoadMapItemsTask;
    public WindSensorsOverlay windSensorsOverlay = null;
    
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
            map.getController().setZoom(12);
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
        /* Add the Wind Sensors overlay to our map */
        LoadMapItemsTask = (LoadMapItems) new LoadMapItems(Omnimap.this, map).execute((Object)null);
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    
        /* Cancel any background fetches for data */
        if(!LoadMapItemsTask.isCancelled()) {
            LoadMapItemsTask.cancel(true);
        }
        /* These next three lines are an attempt to prevent "java.lang.OutOfMemoryError: bitmap size exceeds VM budget"
         * Details here:
         * http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android */
        map.getOverlays().clear();
        unbindDrawables(findViewById(R.id.mapParent));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
