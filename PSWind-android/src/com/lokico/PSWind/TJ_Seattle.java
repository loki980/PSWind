package com.lokico.PSWind;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TJ_Seattle extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tj_seattle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WebView engine = (WebView) findViewById(R.id.web_engine);
        engine.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        engine.getSettings().setAppCacheEnabled(false);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.setBackgroundColor(Color.parseColor("#699ccf"));
        engine.loadUrl("http://pskite.org/looptest/android_tj_seattle.php");  
    }
}
