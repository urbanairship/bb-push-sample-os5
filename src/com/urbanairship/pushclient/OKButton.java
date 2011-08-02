/**
 * PictureBackgroundButtonField.java
 *
 * Copyright © 1998-2009 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.urbanairship.pushclient;

import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;

/**
 * Custom button field that shows how to use images as button backgrounds.
 */
public class OKButton extends Field 
{  
    private Bitmap _currentPicture;
    private Bitmap _onPicture = Bitmap.getBitmapResource("btn_ok_alt.png");
    private Bitmap _offPicture = Bitmap.getBitmapResource("btn_ok.png");
        
    /**
     * Constructor.
     * @param text The text to be displayed on the button
     * @param style Combination of field style bits to specify display attributes 
     */
    public OKButton(long style) 
    {
        super(style);
        _currentPicture = _offPicture;
    }
    
    /**
     * Field implementation.
     * @see net.rim.device.api.ui.Field#getPreferredHeight()
     */
    public int getPreferredHeight() 
    {
    	return _onPicture.getHeight() + 5;
    }

    /**
     * Field implementation.
     * @see net.rim.device.api.ui.Field#getPreferredWidth()
     */
    public int getPreferredWidth() 
    {
    	return _onPicture.getWidth();
    }
    
    /**
     * Field implementation.  Changes the picture when focus is gained.
     * @see net.rim.device.api.ui.Field#onFocus(int)
     */
    protected void onFocus(int direction) 
    {
        _currentPicture = _onPicture;
        invalidate();
    }

    /**
     * Field implementation.  Changes picture back when focus is lost.
     * @see net.rim.device.api.ui.Field#onUnfocus()
     */
    protected void onUnfocus() 
    {
        _currentPicture = _offPicture;
        invalidate();
    }
    
    /**
     * Field implementation.  
     * @see net.rim.device.api.ui.Field#drawFocus(Graphics, boolean)
     */
    protected void drawFocus(Graphics graphics, boolean on) 
    {
        // Do nothing
    }
    
    /**
     * Field implementation.
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(int width, int height) 
    {
        setExtent(Math.min( width, getPreferredWidth()), 
        Math.min( height, getPreferredHeight()));
    }

    /**
     * Field implementation.
     * @see net.rim.device.api.ui.Field#paint(Graphics)
     */
    protected void paint(Graphics graphics) 
    {       
        graphics.drawBitmap(0, 5, getWidth(), getHeight(), _currentPicture, 0, 0);
    }
        
    /**
     * Overridden so that the Event Dispatch thread can catch this event
     * instead of having it be caught here..
     * @see net.rim.device.api.ui.Field#navigationClick(int, int)
     */
    protected boolean navigationClick(int status, int time) 
    {
        fieldChangeNotify(1);
        return true;
    }
    
}
