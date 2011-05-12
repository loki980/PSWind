package com.lokico.PSWind;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.lokico.PSWind.SensorDataSet.SensorData;

public class WindSensorsOverlay extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private Drawable marker = null;
	private PopupPanel panel;
	private SensorDataSet mySensorDataSet;
	private MapView map;
	private final String MY_DEBUG_TAG = "WindFetcherFail";

	public WindSensorsOverlay(Context context, MapView map, Drawable marker, SensorDataXMLHandler mySensorDataXMLHandler) {
		super(marker);
		this.marker = marker;
		this.map = map;
		panel = new PopupPanel(context, R.layout.popup);
		
		/* Create a new TextView to display the parsing result later. */
		try {


			/* Our ExampleHandler now provides the parsed data to us. */
			mySensorDataSet = mySensorDataXMLHandler.getParsedData();
			SensorData sd;
			String arrowPath = "";
			Long twoHoursAgoInSec = (long)0;
			Time currTime = new Time();
			Time theSensorTime = new Time();
			currTime.setToNow();
			twoHoursAgoInSec = currTime.toMillis(true) / 1000 - (60*60*2);
			
			for (int i = 0; i < mySensorDataSet.getSensorDataSize(); i++) {
				sd = mySensorDataSet.getSensorByIndex(i);		
				Integer resID = 0;
				
				theSensorTime.set((long)sd.timestamp * 1000);

				Long minutesAgo = (twoHoursAgoInSec + (60*60*2) - sd.timestamp) / 60;
				if(minutesAgo > 60*24*2) {
					/* If the sensor hasn't been updated in 2 days, remove it from the overlay */
					continue;
				} else if (minutesAgo > 180) {
					/* If the sensor hasn't been updated in 3 hours, mark it as stale */
					sd.isStale = true;
					arrowPath = "drawable/sensormarker_ml_1";
				} else {
					/* arrow */
					arrowPath = "drawable/sensormarker_m";
					if (sd.wind > 19) {
						/* Prefix for red arrow */
						arrowPath += "r";
					} else if (sd.wind > 13) {
						/* Prefix for green arrow */
						arrowPath += "g";
					} else {
						/* Prefix for grey arrow */
						arrowPath += "l";
					}

					/* Append the angle of wind direction */
					arrowPath += "_" + Integer.toString(sd.angle);
				}

				resID = context.getResources().getIdentifier(arrowPath, null,
						context.getPackageName());
				Assert.assertTrue("Resouce not found: " + arrowPath,
						resID != 0);
				
				/* Combine the two icons that make the overlayitem's marker */
				Drawable[] layers = new Drawable[2];
				layers[0] = context.getResources().getDrawable(resID);
				
				/* number */
				if (sd.wind > 99) {
					/* We have no overlay for 100+ mph */
					sd.wind = 99;
				}
				
				if(sd.isStale) {
					resID = context.getResources().getIdentifier(
							"drawable/sensormarker_m", null,
							context.getPackageName());
					Assert.assertTrue(
							"Resouce not found: drawable/sensormarker_m", resID != 0);
					layers[1] = context.getResources().getDrawable(resID);
				} else if (sd.wind >= 0 && sd.wind < 100) {
					resID = context.getResources().getIdentifier(
							"drawable/sensormarker_m"
									+ Integer.toString(sd.wind), null,
							context.getPackageName());
					Assert.assertTrue(
							"Resouce not found: drawable/sensormarker_m"
									+ Integer.toString(sd.wind), resID != 0);
					layers[1] = context.getResources().getDrawable(resID);
				} else {
					/* If the wind is not valid, skip this marker */
					continue;
				}

				LayerDrawable layerDrawable = new LayerDrawable(layers);
				
				OverlayItem overlayItem = null;
				if(sd.gust == 0) {
					/* Just set the gust reading to the wind reading */
					sd.gust = sd.wind;
				}
				
				overlayItem = new OverlayItem(getPoint(sd.lat,
						sd.lon), sd.id, sd.label + "\n"
						+ Integer.toString(sd.wind) + "g"
						+ Integer.toString(sd.gust) +
						" ("+ minutesAgo + " min ago)");
				
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
	public boolean onTap(GeoPoint p, MapView mapView) {
		if(super.onTap(p, mapView)) {
			return true;
		} else {
			panel.hide();
			return false;
		}
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
	
	private GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}
	
	class PopupPanel {
		View popup;
		boolean isVisible = false;
		boolean popupClicked = false;

		PopupPanel(Context context, int layout) {
			ViewGroup parent = (ViewGroup) map.getParent();

			popup = ((MapActivity) context).getLayoutInflater().inflate(layout, parent, false);

			popup.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					popupClicked = true;
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