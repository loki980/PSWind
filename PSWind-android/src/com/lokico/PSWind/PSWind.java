package com.lokico.PSWind;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class PSWind extends MapActivity {
	private MapView map = null;
	private long lastTouchTime = -1;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		map = (MapView) findViewById(R.id.map);

		/* Center map around Seattle */
		map.getController().setCenter(getPoint(47.7805, -122.3822));
		map.getController().setZoom(10);
		map.setBuiltInZoomControls(true);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();

		queueUpdate();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.refresh:
		    	queueUpdate();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void queueUpdate(){
		Handler myHandler = new Handler();
		// Delay loading the overlay by 1 ms to allow the map to display immediately.
		myHandler.postDelayed(mMyRunnable, 100);
	}
	
	//Here's a runnable/handler combo
	private Runnable mMyRunnable = new Runnable() {
	    public void run() {
			displayWind();
	    }
	};

	private void displayWind() {
		/* Add the Wind Sensors overlay to our map */
		new LoadMapItems(PSWind.this, map).execute((Object)null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.windsensor, menu);
	    return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
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
}
