package com.lokico.PSWind;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Implements a spinning loader while the specified image is loading
 */
public class LoaderImageView extends LinearLayout {

    private static final int COMPLETE = 0;
    private static final int FAILED = 1;

    private Context mContext;
    private Drawable mDrawable;
    private ProgressBar mSpinner;
    private AspectRatioImageView mImage;
    private Boolean threadStarted = new Boolean("FALSE");

    /**
     * This is used when creating the view in XML To have an image load in XML
     * use the tag
     * 'image="http://developer.android.com/images/dialog_buttons.png"'
     * Replacing the url with your desired image Once you have instantiated the
     * XML view you can call setImageDrawable(url) to change the image
     * 
     * @param context
     * @param attrSet
     */
    public LoaderImageView(final Context context, final AttributeSet attrSet) {
        super(context, attrSet);
        final String url = attrSet.getAttributeValue(null, "image");
        if (url != null) {
            instantiate(context, url);
        } else {
            instantiate(context, null);
        }
    }

    /**
     * This is used when creating the view programatically Once you have
     * instantiated the view you can call setImageDrawable(url) to change the
     * image
     * 
     * @param context
     *            the Activity context
     * @param imageUrl
     *            the Image URL you wish to load
     */
    public LoaderImageView(final Context context, final String imageUrl) {
        super(context);
        instantiate(context, imageUrl);
    }

    /**
     * First time loading of the LoaderImageView Sets up the LayoutParams of the
     * view, you can change these to get the required effects you want
     */
    private void instantiate(final Context context, final String imageUrl) {
        mContext = context;

        mImage = new AspectRatioImageView(mContext);
        mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mSpinner = new ProgressBar(mContext);
        mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mSpinner.setIndeterminate(true);

        addView(mSpinner);
        addView(mImage);

        if (imageUrl != null) {
            setImageDrawable(imageUrl);
        }
    }

    /**
     * Set's the view's drawable, this uses the internet to retrieve the image
     * don't forget to add the correct permissions to your manifest
     * 
     * @param imageUrl
     *            the url of the image you wish to load
     */
    public void setImageDrawable(final String imageUrl) {
        if(threadStarted == false) {
            threadStarted = true;
            
            mDrawable = null;
            mSpinner.setVisibility(View.VISIBLE);
            mImage.setVisibility(View.GONE);
        
            new Thread() {
                public void run() {
                    try {
                        mDrawable = getDrawableFromUrl(imageUrl);
                        imageLoadedHandler.sendEmptyMessage(COMPLETE);
                    } catch (MalformedURLException e) {
                        imageLoadedHandler.sendEmptyMessage(FAILED);
                    } catch (IOException e) {
                        imageLoadedHandler.sendEmptyMessage(FAILED);
                    }
                };
            }.start();
        }
    }

    /**
     * Callback that is received once the image has been downloaded
     */
    private final Handler imageLoadedHandler = new Handler(new Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case COMPLETE:
                mImage.setImageBitmap(((BitmapDrawable) mDrawable).getBitmap());
                mImage.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.GONE);
                break;
            case FAILED:
            default:
                // Could change image here to a 'failed' image
                // otherwise will just keep on spinning
                break;
            }
            threadStarted = false;
            return true;
        }
    });

    /**
     * Pass in an image url to get a drawable object
     * 
     * @return a drawable object
     * @throws IOException
     * @throws MalformedURLException
     */
    private static Drawable getDrawableFromUrl(final String url)
            throws IOException, MalformedURLException {
        return Drawable.createFromStream(
                ((java.io.InputStream) new java.net.URL(url).getContent()),
                "name");
    }

}