package com.lokico.PSWind;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class NWKiteMap extends MapActivity {

	private MapView mapView;
	private ProgressBar progress;
	private SpotItemizedOverlay spots;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nwkitemap);
		Launcher.setFullScreen(this, Launcher.FULLSCREEN);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

		} else {
			progress = (ProgressBar) findViewById(R.id.nwkitemap_progress);
			mapView = (MapView) findViewById(R.id.nwkitemap);

			mapView.getController().setCenter(getPoint(47.612007, -122.358642));
			mapView.getController().setZoom(8);
			mapView.setBuiltInZoomControls(true);

			progress.setVisibility(View.VISIBLE);
			new ParseTask().execute("http://maps.google.com/maps/ms?ie=UTF8&au"
					+ "thuser=0&msa=0&output=kml&msid=214913314730054445951.0"
					+ "000011206947d3332bf5");
		}
	}

	private class ParseTask extends
			AsyncTask<String, Void, SpotItemizedOverlay> {

		@Override
		protected SpotItemizedOverlay doInBackground(String... urls) {
			try {
				final URL url = new URL(urls[0]);
				final URLConnection conn = url.openConnection();
				conn.setReadTimeout(5 * 1000);
				conn.connect();

				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				SpotDataXMLHandler handler = new SpotDataXMLHandler(
						NWKiteMap.this);
				xr.setContentHandler(handler);

				xr.parse(new InputSource(url.openStream()));

				return handler.getParsedData();
			}
			/*
			 * catch(IOException e) {
			 * 
			 * } catch (ParserConfigurationException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); } catch (SAXException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(SpotItemizedOverlay result) {
			if (result == null) {
				Toast.makeText(NWKiteMap.this, "Problem", Toast.LENGTH_SHORT)
						.show();
			} else {
				spots = result;
				mapView.getOverlays().add(spots);
				progress.setVisibility(View.INVISIBLE);
				mapView.postInvalidate();
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public static GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}
}
