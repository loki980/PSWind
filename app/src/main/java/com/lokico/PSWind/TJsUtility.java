package com.lokico.PSWind;

import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Helper class for the TJs <code>Activity</code>.
 */
public class TJsUtility {
    /**
     * Set the common WebView settings for the TJ's activities.
     *
     * @param webView
     */
    static void setTJsWebViewOptions(WebView webView) {
        // Using no cache settings since TJs updates several times a day.
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.parseColor("#699ccf"));
    }
}
