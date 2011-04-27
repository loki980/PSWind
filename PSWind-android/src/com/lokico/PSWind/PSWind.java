package com.lokico.PSWind;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Assert;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.lokico.PSWind.ParsedExampleDataSet.SensorData;

public class PSWind extends MapActivity {
	private MapView map = null;
	private MyLocationOverlay me = null;
	private long lastTouchTime = -1;
	private final String MY_DEBUG_TAG = "WindFetcherFail";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		map = (MapView) findViewById(R.id.map);

		/* Center map around Seattle */
		map.getController().setCenter(getPoint(47.7805, -122.3822));
		map.getController().setZoom(10);
		map.setBuiltInZoomControls(true);

		/*
		 * Default marker for the wind sensor overlay. Will probably never be
		 * used.
		 */
		Drawable marker = getResources().getDrawable(
				R.drawable.sensormarker_ml_1);

		/*
		 * Not sure what this does, but apparently things aren't drawn right if
		 * it's not set this way
		 */
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		/* Add the Wind Sensors overlay to our map */
		map.getOverlays().add(new WindSensorsOverlay(marker));

		/* Needed for the compass */
		me = new MyLocationOverlay(this, map);
		map.getOverlays().add(me);
	}

	@Override
	public void onResume() {
		super.onResume();

		me.enableCompass();
		me.enableMyLocation();
	}

	@Override
	public void onPause() {
		super.onPause();

		me.disableCompass();
		me.disableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return (false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_S) {
			map.setSatellite(!map.isSatellite());
			return (true);
		} else if (keyCode == KeyEvent.KEYCODE_Z) {
			map.displayZoomControls(true);
			return (true);
		}

		return (super.onKeyDown(keyCode, event));
	}

	private GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			long thisTime = System.currentTimeMillis();
			if (thisTime - lastTouchTime < 250) {
				// Double tap
				lastTouchTime = -1;
				map.getController().zoomInFixing((int) ev.getX(),
						(int) ev.getY());
			} else {
				// Too slow
				lastTouchTime = thisTime;
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	private class WindSensorsOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private PopupPanel panel = new PopupPanel(R.layout.popup);
		private ParsedExampleDataSet parsedExampleDataSet;

		public WindSensorsOverlay(Drawable marker) {
			super(marker);
			this.marker = marker;

			/* Create a new TextView to display the parsingresult later. */
			try {
				/* Create a URL we want to load some xml-data from. */
				URL url = new URL(
						"http://windonthewater.com/xx-xml-w5.php?s=wa");

				/* Get a SAXParser from the SAXPArserFactory. */
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();

				/* Get the XMLReader of the SAXParser we created. */
				XMLReader xr = sp.getXMLReader();
				/* Create a new ContentHandler and apply it to the XML-Reader */
				ExampleHandler myExampleHandler = new ExampleHandler();
				xr.setContentHandler(myExampleHandler);

				/* Parse the xml-data from our URL. */
				xr.parse(new InputSource(url.openStream()));
				/* Parsing has finished. */

				/* Our ExampleHandler now provides the parsed data to us. */
				parsedExampleDataSet = myExampleHandler.getParsedData();
				SensorData sd;
				String arrowPath = "";

				for (int i = 0; i < parsedExampleDataSet.getSensorDataSize(); i++) {
					sd = parsedExampleDataSet.getSensorByIndex(i);
					Integer resID = 0;

					/* Combine the two icons that make the overlayitem's marker */
					Drawable[] layers = new Drawable[2];

					/* arrow */
					arrowPath = "drawable/sensormarker_m";
					if (sd.wind > 19) {
						arrowPath += "r";
					} else if (sd.wind > 13) {
						arrowPath += "g";
					} else {
						arrowPath += "l";
					}
					arrowPath += "_" + Integer.toString(sd.angle);
					resID = getResources().getIdentifier(arrowPath, null,
							getPackageName());
					Assert.assertTrue("Resouce not found: " + arrowPath,
							resID != 0);
					layers[0] = getResources().getDrawable(resID);

					/* number */
					if (sd.wind >= 0 && sd.wind < 100) {
						resID = getResources().getIdentifier(
								"drawable/sensormarker_m"
										+ Integer.toString(sd.wind), null,
								getPackageName());
						Assert.assertTrue(
								"Resouce not found: drawable/sensormarker_m"
										+ Integer.toString(sd.wind), resID != 0);

						layers[1] = getResources().getDrawable(resID);
					} else {
						/* If the wind is not valid, skip this marker */
						continue;
					}

					LayerDrawable layerDrawable = new LayerDrawable(layers);
					
					OverlayItem overlayItem = null;
					if(sd.gust == 0) {
						/* Just set the gust reading to the wind reading */
						overlayItem = new OverlayItem(getPoint(sd.lat,
								sd.lon), sd.id, sd.label + "\n"
								+ Integer.toString(sd.wind) + "g"
								+ Integer.toString(sd.wind));
					} else {
						/* Use the legit gust reading */
						overlayItem = new OverlayItem(getPoint(sd.lat,
								sd.lon), sd.id, sd.label + "\n"
								+ Integer.toString(sd.wind) + "g"
								+ Integer.toString(sd.gust));
					}
					layerDrawable.setBounds(0, 0,
							layerDrawable.getIntrinsicWidth(),
							layerDrawable.getIntrinsicHeight());
					boundCenter(layerDrawable);
					overlayItem.setMarker(layerDrawable);

					items.add(overlayItem);
				}
			} catch (Exception e) {
				/* Display any Error to the GUI. */
				Log.e(MY_DEBUG_TAG, "WindFetchError", e);
			}

			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);
			boundCenter(marker);
		}

		@Override
		protected boolean onTap(int i) {
			OverlayItem item = getItem(i);
			GeoPoint geo = item.getPoint();
			Point pt = map.getProjection().toPixels(geo, null);

			View view = panel.getView();

			/* Display the sensor's name above its graph */
			((TextView) view.findViewById(R.id.sensorname)).setText(item
					.getSnippet());

			/* Load the correct graph for the wind sensor */
			((LoaderImageView) view.findViewById(R.id.windGraph))
					.setImageDrawable("http://windonthewater.com/wg.php?s="
							+ item.getTitle());

			panel.show(pt.y * 2 > map.getHeight());

			return (true);
		}

		@Override
		public int size() {
			return (items.size());
		}
	}

	class PopupPanel {
		View popup;
		boolean isVisible = false;

		PopupPanel(int layout) {
			ViewGroup parent = (ViewGroup) map.getParent();

			popup = getLayoutInflater().inflate(layout, parent, false);

			popup.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					hide();
				}
			});
		}

		View getView() {
			return (popup);
		}

		void show(boolean alignTop) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (alignTop) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				lp.setMargins(0, 20, 0, 0);
			} else {
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.setMargins(0, 0, 0, 60);
			}

			hide();

			((ViewGroup) map.getParent()).addView(popup, lp);
			isVisible = true;
		}

		void hide() {
			if (isVisible) {
				isVisible = false;
				((ViewGroup) popup.getParent()).removeView(popup);
			}
		}
	}
}
