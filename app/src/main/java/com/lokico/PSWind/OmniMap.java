package com.lokico.PSWind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The interactive sensor map <code>Activity</code>.
 */
public class OmniMap extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omni_map);
    }

    /**
     * A unit test helper method.
     *
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return false;
    }
}
