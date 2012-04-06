package com.lokico.PSWind;

import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.google.android.maps.GeoPoint;

public class SpotDataXMLHandler extends DefaultHandler {

	private StringBuffer buffer;
	private SpotItemizedOverlay spotDataSet;
	private boolean inPlacemark;

	private String name;
	private String description;
	private String style;
	private GeoPoint point;

	public SpotDataXMLHandler(Context ctx) {
		spotDataSet = new SpotItemizedOverlay(ctx);
	}

	public SpotItemizedOverlay getParsedData() {
		return spotDataSet;
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startDocument() throws SAXException {
		buffer = new StringBuffer("");
		inPlacemark = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("Placemark")) {
			inPlacemark = true;
		} else if (localName.equals("name")) {
			buffer.delete(0, buffer.length());
		} else if (localName.equals("description")) {
			buffer.delete(0, buffer.length());
		} else if (localName.equals("styleUrl")) {
			buffer.delete(0, buffer.length());
		} else if (localName.equals("coordinates")) {
			buffer.delete(0, buffer.length());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("Placemark")) {
			inPlacemark = false;

			SpotOverlayItem spot = new SpotOverlayItem(point, name,
					"Click for more info...", description, style);
			spotDataSet.addSpot(spot);
		} else if (localName.equals("name")) {
			name = buffer.toString();
		} else if (localName.equals("description")) {
			// Attempt to anchor any links
			String patt = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:"
					+ ",.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			description = buffer.toString().replaceAll(patt,
					"<a href=\"$0\">$0</a>");
		} else if (localName.equals("styleUrl")) {
			style = buffer.toString();
		} else if (localName.equals("coordinates")) {
			String coords = buffer.toString();
			StringTokenizer tok = new StringTokenizer(coords, ",");
			double lon = Double.parseDouble(tok.nextToken());
			double lat = Double.parseDouble(tok.nextToken());
			point = NWKiteMap.getPoint(lat, lon);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (inPlacemark) {
			buffer.append(ch, start, length);
		}
	}
}
