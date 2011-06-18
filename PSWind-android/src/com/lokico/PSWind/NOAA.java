package com.lokico.PSWind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NOAA extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tj_northsound);

        WebView engine = (WebView) findViewById(R.id.web_engine);
        engine.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.setBackgroundColor(Color.parseColor("#699ccf"));
        /* Intercept URL clicks */
        NOAAWebViewClient client = new NOAAWebViewClient();
        engine.setWebViewClient(new NOAAWebViewClient());
        client.loadModifiedURL(engine, "http://www.nwwind.net/regionfcst.php?fcst=70");
    }
    
    private class NOAAWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loadModifiedURL(view, url);
            
            return true;
        }
        
        public void loadModifiedURL(WebView view, String url) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response;
            try {
                response = client.execute(request);

                String html = "";
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null)
                {
                    str.append(line);
                }
                in.close();
                html = str.toString();
                /* Remove NWwind.net */
                html = html.replaceAll("<td class=alignleft colspan=3>.*?</td>", "");
                /* Remove alternate forecasts */
                html = html.replaceAll("<td class=alignleft_bottom_border colspan=3>.*?</td>", "");
                /* Remove tracking scripts */
                html = html.replaceAll("<script>.*?</script>", "");
                /* Remove broken BC links */
                html = html.replaceAll("BC:.*?State:", "State:");
                /* Fix broken whitespace */
                html = html.replaceAll("&nbsp", "&nbsp;");
                
                view.loadDataWithBaseURL("http://nwwind.net", html, "text/html", "UTF-8", "about:blank");
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}