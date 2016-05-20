package com.lokico.PSWind;

import com.google.android.gms.maps.model.LatLng;

/**
 *  This class represents a wind sensor region including: lat/lng, title, html suffix.
 *  A wind sensor region has multiple wind sensors that can be requested in a single request.
 */
public class WindSensorRegion {
    private LatLng mLatLng; // A LatLng to use to place a marker in the region.
    private String mTitle; // A title of the region (e.g. Washington, British Columbia)
    private String mHtmlSuffix; // Used in the http request (e.g. wa for Washington)

    public WindSensorRegion(float lat, float lon, String title, String htmlSuffix) {
        mLatLng = new LatLng(lat, lon);
        mTitle = title;
        mHtmlSuffix = htmlSuffix;
    }

    // Getters
    public String getTitle() {return mTitle;}
    public String getHtmlSuffix() {return mHtmlSuffix;}
    public LatLng getLatLng() {return mLatLng;}
}
