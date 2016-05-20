package com.lokico.PSWind;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The interactive sensor map <code>Activity</code>.
 * This activity uses a Google Map to display wind sensors.
 */
public class OmniMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    static private final String TAG = "OmniMap";
    private GoogleMap mMap;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private CameraPosition mCameraPosition;
    private static final String CAMERA_POSITION = "CAMERA_POSITION";
    private static final String PREFS_NAME = "OMNI_MAP_PREFS";
    private Map<String, WindSensorRegion> mWindSensorRegions;
    private String mWindSensorAreaSelected = "";
    private static final String BASE_WIND_SENSORS_URL =
            "http://windonthewater.com/api/region_wind.php?v=1&k=TEST&r=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init member variables
        mContext = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mWindSensorRegions = new HashMap<>();

        // Init the Google Map
        setContentView(R.layout.activity_omni_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.display_omni_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cancel our request queue
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }

        // Save the camera position
        if (mMap != null) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            CameraPosition cameraPosition = mMap.getCameraPosition();
            LatLng latLng = cameraPosition.target;
            // TODO Use constants for keys
            editor.putFloat("LastZoom", cameraPosition.zoom);
            editor.putFloat("LastLat", (float) latLng.latitude);
            editor.putFloat("LastLng", (float) latLng.longitude);

            // Commit the edits!
            editor.commit();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set the zoom and compass
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        createWindSensorRegionCollection();
        mMap.setOnMarkerClickListener(this);

        // Restore the last camera position
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        // TODO Use constants for keys and resources for default values
        float lastZoom = settings.getFloat("LastZoom", 10.0f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(lastZoom));
        float lastLat = settings.getFloat("LastLat", 48.0035f);
        float lastLng = settings.getFloat("LastLng", -122.228f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLat, lastLng)));

        // Get the raw sensor data
        mWindSensorAreaSelected = "Washington";
        getRawSensorData(BASE_WIND_SENSORS_URL + "wa");

        // Add the geographic region markers.
        // These denote geographic areas where we have wind sensors.
        addMarkersForWindSensorRegions();
    }

    /*
     * Create the collection of WindSensorRegions
     */
    private void createWindSensorRegionCollection() {
        // Create collection of wind sensor regions
        WindSensorRegion.createWindSensorRegionsCollection(mWindSensorRegions);
    }

    /*
     * Add the markers for each WindSensorRegion
     */
    private void addMarkersForWindSensorRegions() {
        if (mMap != null && mWindSensorRegions != null) {
            // Add the wind sensor region markers to the map
            for (WindSensorRegion windSensorRegion : mWindSensorRegions.values()) {
                if (windSensorRegion.getTitle().equals(mWindSensorAreaSelected)) {
                    // Don't add to map
                } else {
                    mMap.addMarker(new MarkerOptions().title(windSensorRegion.getTitle())
                            .position(windSensorRegion.getLatLng()));
                }
            }
        }
    }

    /**
     * A unit test helper method.
     *
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return true;
    }

    // Get the raw sensor data (xml) in the background
    // TODO Consider using RxJava and Retrofit
    private String getRawSensorData(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String html) {
                        // Parse the data
                        new ParseTask().execute(html);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse " + error.toString());
            }
        });

        // Tag and then add the request to the RequestQueue.
        stringRequest.setTag(this);
        mRequestQueue.add(stringRequest);

        return null;
    }

    // Add wind sensor markers to the map
    static private void addMarkers(Context context, GoogleMap map, List<WindSensor> windSensorList) {
        if (windSensorList != null && !windSensorList.isEmpty()) {
            long startTime = System.nanoTime();

            int count = 0;
            Matrix matrix = new Matrix();
            Bitmap b = null;
            for (WindSensor windSensor : windSensorList) {
                // Get params
                String resIdBaseName = windSensor.getBaseIconName();
                String resIdSpeedName = windSensor.getSpeedIconName();
                String packageName = context.getPackageName();
                Resources resources = context.getResources();

                // Get the base bitmap for the sensor
                Bitmap bmpBase = BitmapFactory.decodeResource(
                        resources,
                        resources.getIdentifier(
                                resIdBaseName, "drawable", packageName));

                // Create a bitmap for our composite bitmap
                // Base the size on the bmpBase size
                b = Bitmap.createBitmap(bmpBase.getWidth(), bmpBase.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);

                // Set the rotation (wind direction) and pivot point
                matrix.setRotate(windSensor.getDirection(), bmpBase.getWidth() / 2,
                        bmpBase.getHeight() / 2);

                // Draw the wind sensor's color and direction
                c.drawBitmap(bmpBase, matrix, new Paint());
                // Draw the wind sensor's wind speed
                c.drawBitmap(BitmapFactory.decodeResource(
                        resources,
                        resources.getIdentifier(
                                resIdSpeedName, "drawable", packageName)),
                        0, 0, new Paint());

                // Add the wind sensor marker to the map
                map.addMarker(new MarkerOptions()
                        .position(windSensor.getLatLng())
                        .title(windSensor.getTitle())
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(b)));
            }
            long endTime = System.nanoTime();
            Log.d(TAG, "Time to add markers " + (endTime - startTime));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        WindSensorRegion windSensorRegion = mWindSensorRegions.get(title);
        if (windSensorRegion != null) {
            // Save selected area
            mWindSensorAreaSelected = title;
            // Remove existing wind sensor markers
            mMap.clear();
            // TODO
            // Request new wind sensor markers
            getRawSensorData(BASE_WIND_SENSORS_URL + windSensorRegion.getHtmlSuffix());
            addMarkersForWindSensorRegions();
            return true; // Indicates we handled the event
        }
        return false; // Let the default behavior occur
    }

    // Parse the raw sensor data in the background
    // TODO Consider using RxJava
    private class ParseTask extends AsyncTask<String, Void, List<WindSensor>> {
        @Override
        protected List<WindSensor> doInBackground(String... html) {
            List<WindSensor> list = null;
            try {
                list = WindSensorParser.parseRawSensorData(html[0]);
            } catch (XmlPullParserException e) {
            } catch (IOException e) {
            } finally {
            }
            return list;
        }

        @Override
        protected void onPostExecute(List windSensorList) {
            addMarkers(mContext, mMap, windSensorList);
        }
    }
}
