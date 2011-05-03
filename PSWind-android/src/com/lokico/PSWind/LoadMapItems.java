package com.lokico.PSWind;

import java.util.List;

import android.os.AsyncTask;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LoadMapItems extends AsyncTask<Integer, Integer, List<ItemizedOverlay>> {

	@Override
	protected List<ItemizedOverlay> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		return null;
	}
/*
	private MapView view;

	public LoadMapItems(MapView view) {
		this.view = view;
	}

	public List<ItemizedOverlay> doInBackground(Integer... params) {
		int left = params[0];
		int top = params[1];
		int right = params[2];
		int bottom = params[3];

		return convertToItemizedOverlay(someService.loadSomething(left, top,
				right, bottom));
	}

	private List<ItemizedOverlay> convertToItemizedOverlay(
			List<SomeObject> objects) {
		// ... fill this out to convert your back end object to overlay items
	}

	public void onPostExecute(List<ItemizedOverlay> items) {
		List<Overlay> mapOverlays = view.getOverlays();
		for (ItemizedOverlay item : items) {
			mapOverlays.add(item);
		}
	}
	*/
}
