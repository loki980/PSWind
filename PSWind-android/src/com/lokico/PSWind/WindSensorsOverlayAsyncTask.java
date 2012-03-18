package com.lokico.PSWind;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.maps.MapView;

public class WindSensorsOverlayAsyncTask extends AsyncTask<URL, Object, Object> {
    private Context ctx;
    private MapView map;
    private Omnimap omap;
    private Drawable marker;
    private WindSensorsDataXMLHandler myWindSensorDataXMLHandler = null;
    private Boolean failed = false;
    private Boolean asyncTaskRunning = false;
    private Boolean suppressRefreshedMsgOnce;

    public WindSensorsOverlayAsyncTask(Context context, MapView map) {
        ctx = context;
        this.map = map;
        omap = (Omnimap)ctx;
    }

    //Here's a runnable/handler combo
    private Runnable mMyRunnable = new Runnable() {
        public void run() {
            new WindSensorsOverlayAsyncTask(ctx, map).execute(omap.previousURL);
        }
    };

    @Override
    protected Object doInBackground(URL... urls) {
        /* Prevents more than one background request for data */
        if (asyncTaskRunning) {
            return null;
        } else {
            asyncTaskRunning = true;
        }
        
        /* If we're refreshing the same data, display the toast, as we want to inform the
         * user something actually happened, even though it might not look like it */
        if(omap.previousURL == null || !omap.previousURL.sameFile(urls[0])){
            suppressRefreshedMsgOnce = true;
        } else {
            suppressRefreshedMsgOnce = false;
        }
        omap.previousURL = urls[0];
        
        /*
         * Default marker for the wind sensor overlay. Will probably never be
         * used.
         */
        marker = ctx.getResources().getDrawable(R.drawable.marker_no_wind);

        /*
         * Not sure what this does, but apparently things aren't drawn right if
         * it's not set this way
         */
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        
        SAXParserFactory spf;
        
        /* Create a URL we want to load some xml-data from. */
        try {
            //URL url = new URL(urls);

            /* Get a SAXParser from the SAXPArserFactory. */
            spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
    
            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
            
            /* Create a new ContentHandler and apply it to the XML-Reader */
            myWindSensorDataXMLHandler = new WindSensorsDataXMLHandler();
            xr.setContentHandler(myWindSensorDataXMLHandler);
    
            URLConnection conn = urls[0].openConnection();

            // setting these timeouts ensures the client does not deadlock indefinitely
            // when the server has problems.
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            
            /* Parse the xml-data from our URL - this will send it to myWindSensorDataXMLHandler */
            xr.parse(new InputSource(conn.getInputStream()));
            /* Parsing has finished. */
            
            failed = false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            failed = true;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            failed = true;
        } catch (SAXException e) {
            e.printStackTrace();
            failed = true;
        } catch (IOException e) {
            e.printStackTrace();
            failed = true;
        }

        return null;
    }

    /* Runs after the doInBackground() completes */
    @Override
    protected void onPostExecute(Object result) {        
        if(myWindSensorDataXMLHandler != null && !failed) {
            /* If there's an existing windSensorOverlay, we need to set drawables to null to prevent out of memory crashes */
            if(omap.windSensorsOverlay != null) {
                omap.windSensorsOverlay.clearDrawables();
                map.getOverlays().remove(omap.windSensorsOverlay);
                System.gc();
            }
            
            /* Add the new overlay */
            omap.windSensorsOverlay = new WindSensorsOverlay(ctx, map, marker, myWindSensorDataXMLHandler);
            map.getOverlays().add(omap.windSensorsOverlay);
            map.invalidate();
        } else {
            failed = true;
        }

        if(failed && omap.getOverlayRetries() > 2) {
            omap.setOverlayRetries(0);
            Toast.makeText(ctx, "Failed 3 times, suspending...", Toast.LENGTH_SHORT).show();
        } else if (failed) {
            omap.setOverlayRetries(omap.getOverlayRetries() + 1);
            Toast.makeText(ctx, "Refresh failed.  Retrying...", Toast.LENGTH_SHORT).show();

            // Delay loading the overlay by 1 ms to allow the map to display immediately.
            Handler myHandler = new Handler();
            myHandler.postDelayed(mMyRunnable, 3000);
        } else {
            if(!suppressRefreshedMsgOnce || (omap.getOverlayRetries() > 0)) {
                Toast.makeText(ctx, "Sensor data refreshed", Toast.LENGTH_SHORT).show();
            }
            suppressRefreshedMsgOnce = false;
            omap.setOverlayRetries(0);
        }
        
        /* Allow the task to be run again */
        asyncTaskRunning = false;
        
        super.onPostExecute(result);
    }
}