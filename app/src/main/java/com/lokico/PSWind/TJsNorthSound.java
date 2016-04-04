package com.lokico.PSWind;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TJsNorthSound extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tjs_north_sound);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setBackgroundColor(Color.parseColor("#699ccf"));
        myWebView.loadUrl("http://pskite.org/looptest/android_tj_northsound.php");
    }

    static boolean isActivityImplemented() {
        return true;
    }
}
