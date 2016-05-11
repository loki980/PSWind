package com.lokico.PSWind;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The interactive sensor map <code>Activity</code>.
 */
public class OmniMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        addMarkers(mMap);
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

    static private void parseRawSensorData(String rawSensorData) {

    }

    static private void addMarkers(GoogleMap map) {
        //lat="48.0194" lng="-122.334
        LatLng jetty = new LatLng(48.0194, -122.334);
        map.addMarker(new MarkerOptions().position(jetty).title("Hat Island")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_high_wind)));
    }
}
