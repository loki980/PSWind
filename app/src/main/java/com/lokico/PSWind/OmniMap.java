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
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Adjust camera
        // Jetty Island: lat="48.0035" lng="-122.228"
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.0035, -122.228)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo((float) 10.0));
        mTtask.execute(new String[] { "http://somewebsite" });
    }

    /**
     * A unit test helper method.
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return false;
    }

    // TODO
    static private String getRawSensorData() {
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
                // TODO Test
                count++;
                if (count > 200) {
                    break;
                }

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
        protected String doInBackground(String... urls) {
            try {
                WindSensorParser.parseRawSensorData2("", mWindSensorList);
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
