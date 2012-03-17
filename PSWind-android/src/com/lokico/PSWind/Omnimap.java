package com.lokico.PSWind;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;
import com.lokico.PSWind.WindSensorsOverlay.PopupPanel;

public class Omnimap extends MapActivity {
    private MapView map = null;
    private MyLocationOverlay locationOverlay = null;
    private long lastTouchTime = -1;
    private int overlayRetries = 0;
    public PopupPanel panel;
    private WindSensorsOverlayAsyncTask LoadMapItemsTask;
    public WindSensorsOverlay windSensorsOverlay = null;
    
    /* Only for use in checkForRegionSwitch() */
    private Location oldLocation;
    
    public enum Region {
        NORTHWEST      ("nw", 45.643502,-122.285951),
        CALIFORNIA     ("ca", 40.051462,-122.033266),
        MEXICO         ("mx", 1, 2),
        HAWAII         ("hi", 1, 2),
        TEXAS          ("tx", 1, 2),
        MONTANA        ("mo", 1, 2),
        GREAT_LAKES    ("gl", 1, 2),
        LOUISIANA      ("la", 1, 2),
        MASSACHUSETTS  ("ma", 1, 2),
        NEW_JERSEY     ("nj", 1, 2),
        CHESAPEAKE_BAY ("cb", 1, 2),
        NORTH_CAROLINA ("nc", 1, 2),
        SCG            ("gc", 1, 2),
        FLORIDA        ("fl", 1, 2),
        CARIBBEAN      ("cs", 1, 2);
        //FINLAND        ("??", 1, 2);
                
        // telnet localhost 5554
        // then
        // geo fix -82.411629 28.054553
        
        public final String baseWindSensorURL = "http://windonthewater.com/api/region_wind.php?v=1&k=TEST&r=";
        
        public URL windSensorURL;
        public final double latitude;
        public final double longitude;
        private Region(String RegionCode, double latitude, double longitude) {
            try {
                this.windSensorURL = new URL(this.baseWindSensorURL + RegionCode);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omnimap);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);

        /* Add a marker indicating the user's location */
        locationOverlay = new MyLocationOverlay(this, map);
        locationOverlay.runOnFirstFix(centerAroundFix);
        map.getOverlays().add(locationOverlay);
    }

    private void checkForLargePanning() {
        Projection projection = map.getProjection();
        int y = map.getHeight() / 2; 
        int x = map.getWidth() / 2;

        GeoPoint geoPoint = projection.fromPixels(x, y);
        double centerLatitude = (double)geoPoint.getLatitudeE6() / (double)1E6;
        double centerLongitude = (double)geoPoint.getLongitudeE6() / (double)1E6;
        
        Location location = new Location("");
        location.setLatitude(centerLatitude);
        location.setLongitude(centerLongitude);
        
        if(oldLocation == null) {
            oldLocation = location;
        }
        
        /* If we're 10km different, check for region switch */
        if(location.distanceTo(oldLocation) > 10000) {
            checkForRegionSwitch(centerLatitude, centerLongitude);
            oldLocation = location;
        }
    }
    
    private void checkForRegionSwitch(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            /* Problem is that getlat/lon returns int and getfromLocation and getfromlocation expects double (float) */
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            addresses = geocoder.getFromLocation(45.643502, -122.285951, 1);
            if(addresses != null) {
                addresses = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Runnable centerAroundFix = new Runnable() {
        public void run() {
            /* Jump to current location and set zoom accordingly */
            map.getController().animateTo(locationOverlay.getMyLocation());
            map.getController().setZoom(12);
        }
    };
    
    /* Used by async task to know how many times to retry before giving up */
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
        
        queueWindSensorsUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();

        /* Hide any panel if it's open */
        if(panel != null) {
            panel.hide();
        }
        
        locationOverlay.disableMyLocation();
    }
    
    /* Handles creation of menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        return true;
    }

    /* Handle item selection in menu - currently only 'refresh' option available */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                queueWindSensorsUpdate();
                return true;
            case R.id.satToggle:
                map.setSatellite(!map.isSatellite());
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /* Next three functions are a roundabout way to let the map load before the sensor overlay is ready */
    public void queueWindSensorsUpdate(){
        Handler myHandler = new Handler();
        // Delay loading the overlay by 100 ms to allow the map to display immediately.
        myHandler.postDelayed(windSensorsUpdateRunnable, 100);
    }
    
    private Runnable windSensorsUpdateRunnable = new Runnable() {
        public void run() {
            windSensorsUpdate();
        }
    };

    private void windSensorsUpdate() {
        /* Add the Wind Sensors overlay to our map.  We save the task so we can cancel it on leaving the page */
        LoadMapItemsTask = (WindSensorsOverlayAsyncTask) new WindSensorsOverlayAsyncTask(Omnimap.this, map).execute(Region.CALIFORNIA.windSensorURL);
    }
    
    /* Required implementation - always return false, as we're never going to display a route */
    @Override
    protected boolean isRouteDisplayed() {
        return (false);
    }

    /* Implements double-tap to zoom */
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
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            checkForLargePanning();    ///  call the first block of code here
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
