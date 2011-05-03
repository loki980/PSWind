package com.lokico.PSWind;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SensorDataXMLHandler extends DefaultHandler {

	private SensorDataSet myParsedExampleDataSet = new SensorDataSet();

	public SensorDataSet getParsedData() {
		return this.myParsedExampleDataSet;
	}

	@Override
	public void startDocument() throws SAXException {
		this.myParsedExampleDataSet = new SensorDataSet();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		if (localName.equals("marker")) {
			// Extract Attributes
			myParsedExampleDataSet.addSensorData(atts.getValue("id"),
					atts.getValue("label"),
					Integer.parseInt(atts.getValue("timestamp")),
					Float.parseFloat(atts.getValue("lat")),
					Float.parseFloat(atts.getValue("lng")),
					Integer.parseInt(atts.getValue("wind")),
					Integer.parseInt(atts.getValue("gust")),
					Integer.parseInt(atts.getValue("angle")));
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		// Nothing to do here
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		// Nothing to do here
	}
}