package com.lokico.PSWind;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * The interactive sensor map <code>Activity</code>.
 */
public class OmniMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<WindSensor> mWindSensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omni_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.display_omni_map);
        mapFragment.getMapAsync(this);
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

        // Add a marker at Jetty and move the camera
        LatLng jetty = new LatLng(48.008154, -122.229781);
        mMap.addMarker(new MarkerOptions().position(jetty).title("Marker in Jetty"));

        // TODO Move to background
        // Parse the response
        mWindSensorList = new ArrayList<WindSensor>();
        parseRawSensorData("", mWindSensorList);

        // Add the parsed sensor data
        addMarkers(this, mMap,mWindSensorList);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jetty));
        //addMarkers(mMap);
        mMap.moveCamera(CameraUpdateFactory.zoomTo((float) 10.0));
    }

    /**
     * A unit test helper method.
     *
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return false;
    }

    static private String getRawSensorData() {
        return null;
    }

    static private void parseRawSensorData(String rawSensorData, List<WindSensor> windSensors) {
        // TODO Test data
        /*
        <markers>
<marker id="WOTW07" label="Hat Island" lat="48.0194" lng="-122.334" wind="1" gust="3" angle="225" timestamp="1462988635"/>
<marker id="WA010" label="Locust Beach" lat="48.7767" lng="-122.562" wind="3" gust="5" angle="337" timestamp="1462988340"/>
<marker id="PBFW1" label="Padilla Bay" lat="48.4639" lng="-122.468" wind="2" gust="4" angle="315" timestamp="1462984200"/>
<marker id="KNUW" label="Whidbey Island" lat="48.3492" lng="-122.651" wind="7" gust="0" angle="135" timestamp="1462985760"/>
<marker id="WA001" label="Jetty Island" lat="48.0035" lng="-122.228" wind="6" gust="3" angle="90" timestamp="1409602200"/>

        *
         */
        //WindSensor windSensor = new WindSensor(lat, lon, title, dir, speed)
        windSensors.add(new WindSensor((float) 48.0194,(float)-122.334, "Hat Island", 225, 10));
        windSensors.add(new WindSensor((float) 48.7767,(float)-122.562, "Locust Beach", 337, 15));
        windSensors.add(new WindSensor((float) 48.4639,(float)-122.468, "Padilla Bay", 315, 20));
        windSensors.add(new WindSensor((float) 48.3492,(float)-122.651, "Whidbey Island", 135,25));
        windSensors.add(new WindSensor((float) 48.0035,(float)-122.228, "Jetty Island", 90, 30));
    }

    static private void addMarkers(Context context, GoogleMap map, List<WindSensor> windSensorList) {
        if (windSensorList != null && !windSensorList.isEmpty()) {
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
                Bitmap b = Bitmap.createBitmap(bmpBase.getWidth(), bmpBase.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);

                // Get the matrix to specify the rotation (wind direction)
                Matrix matrix = new Matrix();
                matrix.setRotate(windSensor.getDirection(), bmpBase.getWidth()/2,
                        bmpBase.getHeight()/2);

                // Draw the wind sensor's color and direction
                c.drawBitmap(bmpBase, matrix,new Paint());
                // Draw the wind sensor's wind speed
                c.drawBitmap(BitmapFactory.decodeResource(
                        resources,
                        resources.getIdentifier(
                                resIdSpeedName, "drawable", packageName)),
                        0,0,new Paint());

                // Add sensor marker to the map
                map.addMarker(new MarkerOptions()
                        .position(windSensor.getLatLng())
                        .title(windSensor.getTitle())
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(b)));
            }
        }
    }
}
