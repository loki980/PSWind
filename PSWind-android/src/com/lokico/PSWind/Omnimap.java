package com.lokico.PSWind;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

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
    private WindSensorsOverlayAsyncTask LoadMapItemsTask = null;
    public WindSensorsOverlay windSensorsOverlay = null;
    public final String baseWindSensorURL = "http://windonthewater.com/api/region_wind.php?v=1&k=TEST&r=";
    public static final HashMap<String, String> regionHash = new HashMap<String, String>();
    private Boolean firstResume = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    public URL previousURL;
    private static Boolean _isDebugBuild = null;
    
    /* Only for use in checkForRegionSwitch() */
    private Location oldLocation;
    private String oldState = "";
    

    // telnet localhost 5554
    // then
    // Florida!
    // geo fix -82.411629 28.054553
    // Jetty!
    // geo fix -122.23068 48.0064
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isDebugBuild(this)) {
            setContentView(R.layout.omnimap_debug);
        } else {
            setContentView(R.layout.omnimap);
        }

        /* Populate our regions map */
        createStateHashMap();
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);

        /* We need to enable the overlay for use with the location button later */
        locationOverlay = new MyLocationOverlay(this, map);
        
        /* This refreshes the wind overlay for the first time.  If we don't delay, the map won't draw until the data is ready. */
        delayedCheckForLargePanning();
        
        /* Add location button to upper right of screen and update location/refresh data on click */
        ImageButton locButton = (ImageButton)findViewById(R.id.googlemaps_select_location);
        locButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                map.getOverlays().add(locationOverlay);
                locationOverlay.runOnFirstFix(centerAroundFix);
                locationOverlay.enableMyLocation();
                delayedCheckForLargePanning();
            }
        });
    }

    /* Correlates state (and sometimes country, in the case of Mexico) names with API abbreviations */
    private void createStateHashMap() {
        regionHash.put("British Colombia",  "nw");
        regionHash.put("Washington",        "nw");
        regionHash.put("Oregon",            "nw");
        regionHash.put("California",        "ca");
        regionHash.put("Mexico",            "mx");
        regionHash.put("Hawaii",            "hi");
        regionHash.put("Texas",             "tx");
        regionHash.put("Montana",           "mt");
        regionHash.put("Illinois",          "gl");
        regionHash.put("Wisconsin",         "gl");
        regionHash.put("Indiana",           "gl");
        regionHash.put("Michigan",          "gl");
        regionHash.put("Ohio",              "gl");
        regionHash.put("Ontario",           "gl");
        regionHash.put("Minnesota",         "gl");
        regionHash.put("Louisiana",         "la");
        regionHash.put("Massachusetts",     "ma");
        regionHash.put("Rhode Island",      "ma");
        regionHash.put("New York",          "ny");
        regionHash.put("New Jersey",        "nj");
        regionHash.put("Delaware",          "nj");
        regionHash.put("Maryland",          "cb");
        regionHash.put("Virginia",          "cb");
        regionHash.put("North Carolina",    "nc");
        regionHash.put("South Carolina",    "gc");
        regionHash.put("Georgia",           "gc");
        regionHash.put("Florida",           "fl");
        /* I don't know how to handle the carribean yet... */
        //regionHash.put("","cs");
    }
    
    public void delayedCheckForLargePanning(){
        Handler myHandler = new Handler();
        // Delay loading the overlay by 100 ms to allow the map to display immediately.
        myHandler.postDelayed(checkForLargePanningRunnable, 100);
    }
    
    private Runnable checkForLargePanningRunnable = new Runnable() {
        public void run() {
            checkForLargePanning();
        }
    };
    
    private void checkForLargePanning() {
        /* Get the lat/lon of the center of the MapView */
        Projection projection = map.getProjection();
        int y = map.getHeight() / 2; 
        int x = map.getWidth() / 2;

        GeoPoint geoPoint = projection.fromPixels(x, y);
        double centerLatitude = (double)geoPoint.getLatitudeE6() / (double)1E6;
        double centerLongitude = (double)geoPoint.getLongitudeE6() / (double)1E6;
        
        Location location = new Location("");
        location.setLatitude(centerLatitude);
        location.setLongitude(centerLongitude);
        
        /* If we're 10km different, check for region switch */
        if(oldLocation == null || location.distanceTo(oldLocation) > 10000) {
            checkForRegionSwitch(centerLatitude, centerLongitude);
            oldLocation = location;
        }
    }
    
    private void checkForRegionSwitch(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            /* If the current state doesn't match the old state, switch.  Should probably compare by regions, not by states
             * since changing from michigan->indiana or washington->oregon causes a toast */
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if((addresses.size() > 0) && (addresses.get(0).getAdminArea() != null) &&
                    !addresses.get(0).getAdminArea().equals(oldState)){
                /* Mexico has states, should probably list them all out in the hashmap instead of doing this hack */
                if(addresses.get(0).getCountryName().equals("Mexico")) {
                    if(!oldState.equals("Mexico")) {
                        oldState = "Mexico";
                        queueWindSensorsUpdate();
                    }
                } else {
                    oldState = addresses.get(0).getAdminArea();
                    queueWindSensorsUpdate();
                }
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
            locationOverlay.disableMyLocation();
            /* This is a different thread.  I need to call this from a thread that has a 'Looper' prepared. */
            mHandler.post(new Runnable() {
                public void run() {
                    /* We delay to allow the new location to update. */
                    delayedCheckForLargePanning();
                }
            });
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
        
        if(firstResume) {
            firstResume = false;
            return;
        }
        
        /* This will refresh the overlay */
        queueWindSensorsUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();

        /* Hide any panel if it's open */
        if(panel != null) {
            panel.hide();
        }
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
        try {
            LoadMapItemsTask = (WindSensorsOverlayAsyncTask) new WindSensorsOverlayAsyncTask(Omnimap.this, map).execute(new URL(baseWindSensorURL+regionHash.get(oldState)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
        if(LoadMapItemsTask != null && !LoadMapItemsTask.isCancelled()) {
            LoadMapItemsTask.cancel(true);
        }
        unbindDrawables(findViewById(R.id.mapParent));
    }

    private void unbindDrawables(View view) {
        /* These next three lines are an attempt to prevent "java.lang.OutOfMemoryError: bitmap size exceeds VM budget"
         * Details here:
         * http://stackoverflow.com/questions/1949066/java-lang-outofmemoryerror-bitmap-size-exceeds-vm-budget-android */
        map.getOverlays().clear();
        
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
        System.gc();
    }
    
    // Define the debug signature hash (Android default debug cert). Code from sigs[i].hashCode()
    protected final static int DEBUG_SIGNATURE_HASH = 227005335;

    // Checks if this apk was built using the debug certificate
    // Used e.g. for Google Maps API key determination (from: http://whereblogger.klaki.net/2009/10/choosing-android-maps-api-key-at-run.html)
    public static Boolean isDebugBuild(Context context) {
        if (_isDebugBuild == null) {
            try {
                _isDebugBuild = false;
                Signature [] sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
                for (int i = 0; i < sigs.length; i++) {
                    if (sigs[i].hashCode() == DEBUG_SIGNATURE_HASH) {
                        _isDebugBuild = true;
                        break;
                    }
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }      
        }
        return _isDebugBuild;
    }
}
