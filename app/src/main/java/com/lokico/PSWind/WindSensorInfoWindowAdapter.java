package com.lokico.PSWind;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * This class handles the custom marker info window behavior where we retrieve and display
 * the wind graph for a selected wind sensor.
 */
public class WindSensorInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Marker mMarkerShowingInfoWindow;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private boolean mWindowUpdated;
    private Bitmap mBitmap = null;
    private Marker mBitmapMarker = null;

    public WindSensorInfoWindowAdapter(Context context, RequestQueue requestQueue) {
        mContext = context;
        mRequestQueue = requestQueue;
        mWindowUpdated = false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        mMarkerShowingInfoWindow = marker;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        // Getting view from the layout file custom_info_window
        View popUp = inflater.inflate(R.layout.custom_info_window, null);

        TextView popUpTitle = (TextView) popUp.findViewById(R.id.title);
        final ImageView popUpImage = (ImageView) popUp.findViewById(R.id.windgraph);

        popUpTitle.setText(marker.getTitle());

        // Do we have the wind graph already for this marker?
        if (mWindowUpdated && mBitmapMarker.equals(mMarkerShowingInfoWindow)) {
            popUpImage.setImageBitmap(mBitmap);
            return popUp;
        } else {
            mWindowUpdated = false;
        }

        // Fetch the wind graph image
        final String imagePath = "http://windonthewater.com/wg.php?s=" + marker.getSnippet() + "&d=0";
        ImageRequest request = new ImageRequest(imagePath,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // Add the image to the popup
                        if (mMarkerShowingInfoWindow != null && mMarkerShowingInfoWindow.isInfoWindowShown()
                                && popUpImage != null) {
                            mBitmapMarker = mMarkerShowingInfoWindow;
                            mBitmap = bitmap;
                            mWindowUpdated = true;
                            mMarkerShowingInfoWindow.hideInfoWindow();
                            mMarkerShowingInfoWindow.showInfoWindow();
                        }
                    }
                }, 0, 0, null, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        // Log an error
                    }
                });
        // Tag and add the request to the queue
        request.setTag(mContext);
        mRequestQueue.add(request);

        // Returning the view containing InfoWindow contents
        return popUp;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Nothing to do here. All the work is done in the getInfoWindow() method
        return null;
    }
}
