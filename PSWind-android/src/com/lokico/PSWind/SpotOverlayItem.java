package com.lokico.PSWind;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class SpotOverlayItem extends OverlayItem {

	private String description;
	private String style;
	private Drawable marker;
	private Context ctx;

	public SpotOverlayItem(GeoPoint point, String title, String snippet,
			String desc, String style) {
		super(point, title, snippet);
		description = desc;
		setStyle(style);
	}
	
	public String getDescription() {
		return description;
	}
	
	private void setStyle(String style) {
		// Annoying to implement currently because Google sucks at being
		// consistant with their style references, so I'm not inclined to fix
		this.style = style;		
	}
}
