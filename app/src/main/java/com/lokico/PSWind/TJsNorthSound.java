package com.lokico.PSWind;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import static com.lokico.PSWind.TJsUtility.*;


/**
 * The TJ's forecast for the North Sound <code>Activity</code>.
 */
public class TJsNorthSound extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tjs_north_sound);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        setTJsWebViewOptions(myWebView);
        myWebView.loadUrl("http://pskite.org/looptest/android_tj_northsound.php");
    }

    /**
     * A unit test helper method.
     *
     * @return <code>true</code> if detailed testing can proceed.
     */
    static boolean isActivityImplemented() {
        return true;
    }
}
