package com.lokico.PSWind;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.format.Time;
//import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
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
    //private final String MY_DEBUG_TAG = "WindFetcherFail";
    private int windGraphDaysAgo = 0;
    private OverlayItem item = null;
    private Context ctx;

    public WindSensorsOverlay(Context context, MapView map, Drawable marker, WindSensorsDataXMLHandler mySensorDataXMLHandler) {
        super(marker);
        this.marker = marker;
        this.map = map;
        ctx = context;
        panel = new PopupPanel(context, R.layout.popup);
        ((Omnimap)ctx).panel = panel;
        
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
                /* Combine the two icons that make the overlayitem's marker */
                Drawable[] layers = new Drawable[2];
                
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
                    arrowPath = "drawable/marker_stale";
                    
                    resID = context.getResources().getIdentifier(arrowPath, null,
                            context.getPackageName());
                    Assert.assertTrue("Resouce not found: " + arrowPath,
                            resID != 0);

                    layers[0] = context.getResources().getDrawable(resID);
                } else {
                    /* arrow */
                    arrowPath = "drawable/";
                    if (sd.wind > 27) {
                        /* Prefix for red arrow */
                        arrowPath += "marker_high_wind";
                    } else if (sd.wind > 19) {
                        /* Prefix for orange arrow */
                        arrowPath += "marker_medium_wind";
                    } else if (sd.wind > 13) {
                        /* Prefix for green arrow */
                        arrowPath += "marker_light_wind";
                    } else {
                        /* Prefix for grey arrow */
                        arrowPath += "marker_no_wind";
                    }
                    
                   resID = context.getResources().getIdentifier(arrowPath, null,
                            context.getPackageName());
                    Assert.assertTrue("Resouce not found: " + arrowPath,
                            resID != 0);
                        
                    /* Rotate to match the wind direction */
                    layers[0] = this.Rotate(BitmapFactory.decodeResource(ctx.getResources(), resID), sd.angle);
                }

                /* number */
                if (sd.wind > 99) {
                    /* We have no overlay for 100+ mph */
                    sd.wind = 99;
                }
                
                if(sd.isStale) {
                    resID = context.getResources().getIdentifier(
                            "drawable/windspeed", null,
                            context.getPackageName());
                    Assert.assertTrue(
                            "Resouce not found: drawable/windspeed", resID != 0);
                    layers[1] = context.getResources().getDrawable(resID);
                } else if (sd.wind >= 0 && sd.wind < 100) {
                    resID = context.getResources().getIdentifier(
                            "drawable/windspeed"
                                    + Integer.toString(sd.wind), null,
                            context.getPackageName());
                    Assert.assertTrue(
                            "Resouce not found: drawable/windspeed"
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
            //Log.e(MY_DEBUG_TAG, "WindFetchError", e);
        }

        populate();
    }
    
    public void clearDrawables() {
        for(OverlayItem oItem : items) {
            oItem.setMarker(null);
        }
    }

    /* Using a canvas element here prevents shrinking due to boundaries when rotating */
    public Drawable Rotate(Bitmap bMap, int degrees) {
        // Create blank bitmap of equal size
        Bitmap canvasBitmap = bMap.copy(Bitmap.Config.ARGB_8888, true);
        canvasBitmap.eraseColor(0x00000000);

        // Create canvas
        Canvas canvas = new Canvas(canvasBitmap);

        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(degrees, canvas.getWidth()/2, canvas.getHeight()/2);

        // Draw bitmap onto canvas using matrix
        canvas.drawBitmap(bMap, rotateMatrix, null);

        return new BitmapDrawable(canvasBitmap); 
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
            windGraphDaysAgo = 0;
            return false;
        }
    }
    
    @Override
    protected boolean onTap(int i) {
        item = getItem(i);
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
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        private GestureDetector gestureDetector;
        public View.OnTouchListener gestureListener;

        PopupPanel(Context context, int layout) {
            ViewGroup parent = (ViewGroup) map.getParent();

            popup = ((MapActivity) context).getLayoutInflater().inflate(layout, parent, false);
            
            // Gesture detection
            gestureDetector = new GestureDetector(new MyGestureDetector());
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {
                        return true;
                    }
                    return false;
                }
            };
            
            popup.setOnTouchListener(gestureListener);
        }
        
        class MyGestureDetector extends SimpleOnGestureListener {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    
                    View view = panel.getView();

                    /* Display the sensor's name above its graph */
                    ((TextView) view.findViewById(R.id.sensorname)).setText(item
                            .getSnippet());
                    
                    // right to left swipe
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if(--windGraphDaysAgo < 0) {
                            windGraphDaysAgo = 0;
                        } else {
                            /* Load the correct graph for the wind sensor */
                            ((LoaderImageView) view.findViewById(R.id.windGraph))
                                    .setImageDrawable("http://windonthewater.com/wg.php?s="
                                            + item.getTitle() + "&d=" + windGraphDaysAgo);
                        }
                    }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if(++windGraphDaysAgo > 5) {
                            windGraphDaysAgo = 5;
                        } else {
                            /* Load the correct graph for the wind sensor */
                            ((LoaderImageView) view.findViewById(R.id.windGraph))
                                    .setImageDrawable("http://windonthewater.com/wg.php?s="
                                            + item.getTitle() + "&d=" + windGraphDaysAgo);
                        }
                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                int i=0;
                i++;
                return true;
            }
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