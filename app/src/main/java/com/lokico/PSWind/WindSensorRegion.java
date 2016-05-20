package com.lokico.PSWind;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

/**
 * This class represents a wind sensor region including: lat/lng, title, html suffix.
 * A wind sensor region has multiple wind sensors that can be requested in a single request.
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
    public String getTitle() {
        return mTitle;
    }

    public String getHtmlSuffix() {
        return mHtmlSuffix;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    // The regions
    public static void createWindSensorRegionsCollection(Map<String, WindSensorRegion> windSensorRegionList) {
        if (windSensorRegionList == null) {
            return;
        }
        String title = "";
        String htmlSuffix = "";
        float lat = 0;
        float lng = 0;

        title = "Oregon";
        lat = 44.663f;
        lng = -122.431f;
        htmlSuffix = "or";
        windSensorRegionList.put(title, new WindSensorRegion(lat, lng, title, htmlSuffix));
        title = "Washington";
        lat = 47.882f;
        lng = -121.849f;
        htmlSuffix = "wa";
        windSensorRegionList.put(title, new WindSensorRegion(lat, lng, title, htmlSuffix));
        title = "British Columbia";
        lat = 49.943f;
        lng = -124.651f;
        htmlSuffix = "bc";
        windSensorRegionList.put(title, new WindSensorRegion(lat, lng, title, htmlSuffix));
        title = "California";
        lat = 37.794f;
        lng = -122.007f;
        htmlSuffix = "ca";
        windSensorRegionList.put(title, new WindSensorRegion(lat, lng, title, htmlSuffix));
    }
}
