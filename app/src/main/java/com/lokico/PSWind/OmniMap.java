package com.lokico.PSWind;

import android.content.Context;
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
    private List<WindSensor> mWindSensorList;
    Context mContext;
    ParseTask mTtask;
    RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omni_map);
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.display_omni_map);
        mapFragment.getMapAsync(this);
        // Init member vars
        mTtask = new ParseTask();
        mWindSensorList = new ArrayList<WindSensor>();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Adjust camera
        // Jetty Island: lat="48.0035" lng="-122.228"
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.0035, -122.228)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo((float) 10.0));

        // Get the raw sensor data
        getRawSensorData("http://windonthewater.com/api/region_wind.php?v=1&k=TEST&r=wa");
    }

    /**
     * A unit test helper method.
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return false;
    }

     private String getRawSensorData(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String html) {
                        // Do something with the response
                        // Parse the data
                        mTtask.execute(html);
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
    private class ParseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... html) {
            try {
                WindSensorParser.parseRawSensorData(html[0], mWindSensorList);
            } catch (XmlPullParserException e) {
            } catch (IOException e) {
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            addMarkers(mContext, mMap, mWindSensorList);
        }
    }
}
