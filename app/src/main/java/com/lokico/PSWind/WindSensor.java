package com.lokico.PSWind;

import com.google.android.gms.maps.model.LatLng;

public class WindSensor {
    LatLng mLatLng;
    String mTitle;
    String mBaseIconName;
    float mDirection;
    float mSpeed;
    String mSpeedIconName;

    // TODO Consider using a builder pattern
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
                        /* Prefix for red arrow */
            mBaseIconName += "marker_high_wind";
        } else if (mSpeed > 19) {
                        /* Prefix for orange arrow */
            mBaseIconName += "marker_medium_wind";
        } else if (mSpeed > 13) {
                        /* Prefix for green arrow */
            mBaseIconName += "marker_light_wind";
        } else {
                        /* Prefix for grey arrow */
            mBaseIconName += "marker_no_wind";
        }

        mSpeedIconName = "windspeed";
        if (mSpeed >= 0 && mSpeed < 100) {
            mSpeedIconName = mSpeedIconName + Integer.toString((int)mSpeed);
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
