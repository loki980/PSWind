package com.lokico.PSWind;

import com.google.android.gms.maps.model.LatLng;
/*
 *  This class represents a wind sensor reading including: lat/lng, title, speed, direction.
 */
public class WindSensor {
    private LatLng mLatLng;
    private String mTitle;
    private String mBaseIconName;
    private float mDirection;
    private float mSpeed;
    private String mSpeedIconName;

    public WindSensor(float lat, float lon, String title, float direction,
                      float speed) {
        mLatLng = new LatLng(lat, lon);
        mTitle = title;
        mDirection = direction;
        mSpeed = speed;

        // Select icons to display itself
        //mBaseIconName = "drawable/";
        mBaseIconName = "";
        if (mSpeed > 27) {
            mBaseIconName += "marker_high_wind"; // red arrow
        } else if (mSpeed > 19) {
            mBaseIconName += "marker_medium_wind"; // orange arrow
        } else if (mSpeed > 13) {
            mBaseIconName += "marker_light_wind"; // green arrow
        } else {
            mBaseIconName += "marker_no_wind"; // grey arrow
        }

        mSpeedIconName = "windspeed";
        if (mSpeed >= 0 && mSpeed < 100) {
            mSpeedIconName = mSpeedIconName + Integer.toString((int)mSpeed);
        } else {
            // Anything out of range will be marked as 0 mph
            mSpeedIconName = mSpeedIconName + Integer.toString((int)0);
        }
    }

    // Getters
    LatLng getLatLng() {return mLatLng;}
    String getTitle() {return mTitle;}
    String getBaseIconName() {return mBaseIconName;}
    String getSpeedIconName() {return mSpeedIconName;}
    float getDirection() {return mDirection;}
    float getSpeed() {return mSpeed;}
}
