package com.lokico.PSWind;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WindSensorParser {

    static final private String TAG = "WindSensorParser";
    private static final String ns = null;

    static public List<WindSensor> parseRawSensorData(String rawSensorData)
            throws XmlPullParserException, IOException {
        List<WindSensor> list = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(rawSensorData));
            parser.nextTag();
            list = readFeed(parser);
        } finally {
        }
        return list;
    }

    static List<WindSensor> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "markers");
        List<WindSensor> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the marker tag
            if (name.equals("marker")) {
                list.add(readMarker(parser));
            } else {
                skip(parser);
            }
        }
        return list;
    }

    static private WindSensor readMarker(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String lat = null;
        String lng = null;
        String title = null;
        String speed = null;
        String direction = null;
        String name = parser.getName();
        //name = parser.getName();

        if (name.equals("marker")) {
            lat = parser.getAttributeValue(null, "lat");
            lng = parser.getAttributeValue(null, "lng");
            title = parser.getAttributeValue(null, "label");
            speed = parser.getAttributeValue(null, "wind");
            direction = parser.getAttributeValue(null, "angle");
        } else {
            //skip(parser);
        }
        parser.next();

        return new WindSensor(Float.parseFloat(lat), Float.parseFloat(lng), title,
                Float.parseFloat(direction), Float.parseFloat(speed));
    }

    static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
