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

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.maps.MapView;

public class LoadMapItems extends AsyncTask<Object, Object, Object> {
	private ProgressDialog dlg;
	private Context ctx;
	private MapView map;
	private Omnimap omap;
	private Drawable marker;
	private SensorDataXMLHandler mySensorDataXMLHandler = null;
	private Boolean failed = false;
	private Boolean asyncTaskRunning = false;

	public LoadMapItems(Context context, MapView map) {
		ctx = context;
		this.map = map;
		omap = (Omnimap)ctx;
	}

	@Override
	protected void onPreExecute() {
		dlg = new ProgressDialog(ctx);
		dlg.setMessage("Loading....");
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Object result) {
		dlg.dismiss();
		if (result instanceof Exception) {
			// show error message
		} else {
			// display data
		}
		
		if(mySensorDataXMLHandler != null && !failed) {
		    WindSensorsOverlay windSensorsOverlay = new WindSensorsOverlay(ctx, map, marker, mySensorDataXMLHandler);
		    map.getOverlays().clear();
		    map.getOverlays().add(windSensorsOverlay);
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
		    omap.setOverlayRetries(0);
            Toast.makeText(ctx, "Sensor data refreshed", Toast.LENGTH_SHORT).show();
		}
		
		/* Allow the task to be run again */
		asyncTaskRunning = false;
		
		super.onPostExecute(result);
	}
    
    //Here's a runnable/handler combo
    private Runnable mMyRunnable = new Runnable()
    {
        public void run()
        {
            new LoadMapItems(ctx, map).execute((Object)null);
        }
    };
    
	@Override
	protected Object doInBackground(Object... params) {
	    /* Prevents more than one background request for data */
	    if (asyncTaskRunning) {
	        return null;
	    } else {
	        asyncTaskRunning = true;
	    }
	    
		/*
		 * Default marker for the wind sensor overlay. Will probably never be
		 * used.
		 */
		marker = ctx.getResources().getDrawable(
				R.drawable.marker_no_wind);

		/*
		 * Not sure what this does, but apparently things aren't drawn right if
		 * it's not set this way
		 */
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		
		System.out.println("new overlay incoming...");
		
		SAXParserFactory spf;
		
		/* Create a URL we want to load some xml-data from. */
		try {
			URL url = new URL(
					"http://windonthewater.com/api/region_wind.php?v=1&r=nw&k=TEST");

			/* Get a SAXParser from the SAXPArserFactory. */
			spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
	
			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			/* Create a new ContentHandler and apply it to the XML-Reader */
			mySensorDataXMLHandler = new SensorDataXMLHandler();
			xr.setContentHandler(mySensorDataXMLHandler);
	
			URLConnection conn = url.openConnection();

			// setting these timeouts ensures the client does not deadlock indefinitely
			// when the server has problems.
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			
			/* Parse the xml-data from our URL. */
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

}