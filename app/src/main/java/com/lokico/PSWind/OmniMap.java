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
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The interactive sensor map <code>Activity</code>.
 */
public class OmniMap extends FragmentActivity implements OnMapReadyCallback {

    static private final String TAG = "OmniMap";
    private GoogleMap mMap;
    Context mContext;
    RequestQueue mRequestQueue;
    private CameraPosition cameraPosition;
    private static final String CAMERA_POSITION = "CAMERA_POSITION";
    private static final String PREFS_NAME = "OmniMapPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omni_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.display_omni_map);
        mapFragment.getMapAsync(this);
        // Init member vars
        mContext = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putString(CAMERA_POSITION, cameraPosition);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }

        // Save the camera position
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        CameraPosition cameraPosition = mMap.getCameraPosition();
        LatLng latLng = cameraPosition.target;
        editor.putFloat("LastZoom", cameraPosition.zoom);
        editor.putFloat("LastLat", (float) latLng.latitude);
        editor.putFloat("LastLng", (float) latLng.longitude);

        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //mMap.setMyLocationEnabled();

        // Restore last camera position
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        float lastZoom = settings.getFloat("LastZoom", 10.0f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(lastZoom));
        float lastLat = settings.getFloat("LastLat", 48.0035f);
        float lastLng = settings.getFloat("LastLng", -122.228f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLat, lastLng)));

        // Get the raw sensor data
        getRawSensorData("http://windonthewater.com/api/region_wind.php?v=1&k=TEST&r=wa");
    }

    /**
     * A unit test helper method.
     *
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return false;
    }


    // Get the raw sensor data in the background
    private String getRawSensorData(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String html) {
                        // Do something with the response
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

                // Get the matrix to specify the rotation (wind direction)
                //Matrix matrix = new Matrix();
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

                // Add sensor marker to the map
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

    // Parse the raw sensor data in background
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
        protected void onPostExecute(List result) {
            addMarkers(mContext, mMap, result);
        }
    }
}
