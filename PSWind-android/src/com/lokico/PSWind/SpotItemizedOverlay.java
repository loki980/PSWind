package com.lokico.PSWind;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.webkit.WebView;

import com.google.android.maps.ItemizedOverlay;

public class SpotItemizedOverlay extends ItemizedOverlay<SpotOverlayItem> {

	private ArrayList<SpotOverlayItem> spots;
	private Context ctx;

	public SpotItemizedOverlay(Context context) {
		super(boundCenterBottom(context.getResources().getDrawable(
				R.drawable.blue_dot)));
		ctx = context;

		spots = new ArrayList<SpotOverlayItem>();
	}
	

	@Override
	protected boolean onTap(int index) {
		SpotOverlayItem spot = spots.get(index);
		AlertDialog.Builder d = new AlertDialog.Builder(ctx);
		d.setTitle(spot.getTitle());
		
		// Put the description in a WebView in an attempt to easily display
		// its funky formatting
		WebView w = new WebView(ctx);
		w.loadData(spot.getDescription(), "text/html", null);
		d.setView(w);
		
		d.show();
		
		return true;
	}

	public void addSpot(SpotOverlayItem spot) {
		spots.add(spot);
		populate();
	}

	@Override
	protected SpotOverlayItem createItem(int i) {
		return spots.get(i);
	}

	@Override
	public int size() {
		return spots.size();
	}
}
