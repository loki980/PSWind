package com.lokico.PSWind;

import com.google.android.gms.maps.model.LatLng;

/**
 *  This class represents a wind sensor area including: lat/lng, title, html suffix.
 *  A wind sensor area has multiple wind sensors that can be requested in a single request.
 */
public class WindSensorArea {
    private LatLng mLatLng;
    private String mTitle;
    private String mHtmlSuffix; // Used in the http request (e.g. wa for Washington)

    public WindSensorArea(float lat, float lon, String title, String htmlSuffix) {
        mLatLng = new LatLng(lat, lon);
        mTitle = title;
        mHtmlSuffix = htmlSuffix;
    }

    // Getters
    public String getTitle() {return mTitle;}
    public String getHtmlSuffix() {return mHtmlSuffix;}
    public LatLng getLatLng() {return mLatLng;}
}
