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

    /*
     * Adds the WindOnTheWater regions
     */
    public static void createWindSensorRegionsCollection(Map<String, WindSensorRegion> windSensorRegionList) {
        if (windSensorRegionList == null) {
            return;
        }

        addRegionToCollection(windSensorRegionList, "Oregon", 44.663f,-122.431f,"or");
        addRegionToCollection(windSensorRegionList, "Washington", 47.882f,-121.849f,"wa");
        addRegionToCollection(windSensorRegionList, "British Columbia", 49.943f,-124.651f,"bc");
        addRegionToCollection(windSensorRegionList, "California", 37.794f,-122.007f,"ca");
        addRegionToCollection(windSensorRegionList, "Mexico", 26.219f,-108.775f,"mx");
        addRegionToCollection(windSensorRegionList, "Hawaii", 20.802f,-156.268f,"hi");
        addRegionToCollection(windSensorRegionList, "Texas", 28.208f,-97.605f,"tx");
        addRegionToCollection(windSensorRegionList, "Montana", 47.594f,-109.799f,"mt");
        addRegionToCollection(windSensorRegionList, "Great Lakes", 45.355f,-84.499f,"gl");
        addRegionToCollection(windSensorRegionList, "Louisiana", 29.837f,-92.435f,"la");
        addRegionToCollection(windSensorRegionList, "Massachusetts", 42.172f,-70.993f,"ma");
        addRegionToCollection(windSensorRegionList, "New York", 40.802f,-73.400f,"ny");
        addRegionToCollection(windSensorRegionList, "New Jersey", 39.844f,-74.324f,"nj");
        addRegionToCollection(windSensorRegionList, "North Carolina", 35.692f,-76.752f,"nc");
        addRegionToCollection(windSensorRegionList, "South Carolina / Georgia", 33.111f,-79.698f,"gc");
        addRegionToCollection(windSensorRegionList, "Florida", 27.491f,-81.521f,"fl");
    }

    // Helper function to add a region to the collection
    private static void addRegionToCollection(Map<String, WindSensorRegion> windSensorRegionList,
                                              String title, float lat, float lng,
                                              String htmlSuffix) {
        windSensorRegionList.put(title, new WindSensorRegion(lat, lng, title, htmlSuffix));
    }
}
