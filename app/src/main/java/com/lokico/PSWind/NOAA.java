package com.lokico.PSWind;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class NOAA extends AppCompatActivity {

    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noa);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setBackgroundColor(Color.parseColor("#699ccf"));
        NOAAWebViewClient client = new NOAAWebViewClient();
        myWebView.setWebViewClient(client);
        client.loadModifiedURL(myWebView, "http://www.nwwind.net/regionfcst.php?fcst=70");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    private class NOAAWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loadModifiedURL(view, url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }

        public void loadModifiedURL(final WebView view, String url) {
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String html) {
                            // Note: (?s) sets the mode to include line breaks
                            // Remove first row in table with the NWWind string
                            // and alternate forecasts
                            html = html.replaceAll("(?s)<td class=alignleft_bottom_border colspan=3>.*?</td>", "");
                            /* Remove tracking scripts */
                            html = html.replaceAll("(?s)<script>.*?</script>", "");
                            /* Remove broken BC links */
                            html = html.replaceAll("(?s)BC:.*?State:", "State:");
                            /* Fix broken whitespace */
                            html = html.replaceAll("&nbsp", "&nbsp;");

                            view.loadDataWithBaseURL("http://nwwind.net", html, "text/html", "UTF-8", "about:blank");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Log error TODO
                }
            });

            // Tag and then add the request to the RequestQueue.
            stringRequest.setTag(this);
            mRequestQueue.add(stringRequest);
        }
    }

    static boolean isActivityImplemented() {
        return false;
    }
}
