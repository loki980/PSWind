package com.lokico.PSWind;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import static com.lokico.PSWind.TJsUtility.setTJsWebViewOptions;

/**
 * The TJ's forecast for Seattle <code>Activity</code>.
 */
public class TJsSeattle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tjs_seattle);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        setTJsWebViewOptions(myWebView);
        myWebView.loadUrl("http://pskite.org/looptest/android_tj_seattle.php");
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
