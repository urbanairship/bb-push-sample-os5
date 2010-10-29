package com.urbanairship.pushclient;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.push.PushApplicationDescriptor;
import net.rim.blackberry.api.push.PushApplicationRegistry;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.UiApplication;

public class PushConnector implements GlobalEventListener { 

	private static final long GUID				= 0x30120912ea356c9cL;
	private static final long PUSH_GUID 		= 0x8aba2a7ecd1ac66cL;
	static final long PUSH_ENABLE_GUID 			= 0x8aba2a7ecd1ac66dL;
	static final long PUSH_DISABLE_GUID 		= 0x8aba2a7ecd1ac66eL;
	
	// Push Application Descriptor
	private PushApplicationDescriptor _pad 	= new PushApplicationDescriptor(Keys.BLACKBERRY_PUSH_APPLICATION_ID, 
																			Keys.BLACKBERRY_PUSH_PORT, 
																			Keys.BLACKBERRY_PUSH_URL, 
																			PushApplicationDescriptor.SERVER_TYPE_BPAS, 
																			ApplicationDescriptor.currentApplicationDescriptor());

	/**
	 * Push Connector for OS 5.X.
	 */
	public PushConnector() {
        // Register our app indicator
        ApplicationIndicatorRegistry reg = ApplicationIndicatorRegistry.getInstance();
        EncodedImage mImage = EncodedImage.getEncodedImageResource("widdle_icon.png");
        ApplicationIcon mIcon = new ApplicationIcon(mImage);
        if (reg.getApplicationIndicator()==null) {
        	reg.register(mIcon, true, false);
        	}
    	
    	// Add a global listener so we can turn off indicators
        UiApplication.getUiApplication().addGlobalEventListener(this);
		}

	/**
	 * writeMessage write message to Console.
	 * 
	 * @param message Message to write to the Console.
	 */
	public void writeMessage(final String message) {
		UiApplication.getApplication().invokeLater(new Runnable() {
			public void run() {
				((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage(message);
				Util.debugPrint(getClass().getName() + " (OS 5.X)", message);
				}
			});
		}
	
	/**
	 * De-Register for Push Service with RIM.
	 */
	public void deRegisterForService() {
		writeMessage("De-Registering for push notifications with RIM");
		try {
			PushApplicationRegistry.unregisterApplication();
		} catch (IllegalArgumentException e) {
			writeMessage("De-Registration failed with exception: " + e.toString());
			}
		}
	
	/**
	 * Register for Push Service with RIM.
	 */
	public void registerForService() {
		writeMessage("Registering for push notifications with RIM");
		try {
			PushApplicationRegistry.registerApplication(_pad);
		} catch (IllegalArgumentException e) {
			writeMessage("Registration failed with exception: " + e.toString());
			}
		}

	/**
	 * Handle inbound notification messages
	 * 
	 * @param message Notification Message
	 */
	void handleMessage(String message) {

        try {
    		// Save the inbound message for later review in the data store
    		UrbanAirshipStore.setNotification(message);

    		// Turn on the LED
            LED.setConfiguration( 500, 250, LED.BRIGHTNESS_50 );
            LED.setState( LED.STATE_BLINKING );

            // Set the indicator on
	        ApplicationIndicatorRegistry reg = ApplicationIndicatorRegistry.getInstance();
	        ApplicationIndicator appIndicator = reg.getApplicationIndicator();
	        appIndicator.setVisible(true);
	        
	        Class cl = null;
			try {
				cl = Class.forName("com.urbanairship.pushclient.UrbanAirshipMain");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				}
			if (cl!=null) {
				InputStream is = cl.getResourceAsStream("/cash.mp3");
				try {
					Player player = Manager.createPlayer(is, "audio/mpeg");
					player.realize();
					player.prefetch();
					player.start();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MediaException e) {
					e.printStackTrace();
					}
				}
			
			Bitmap bm = Bitmap.getBitmapResource("uaiconAlert.png");
			net.rim.blackberry.api.homescreen.HomeScreen.updateIcon(bm, 0);
	                    
            // And let's tell the app we have something for them, and turn on indicators
            ApplicationManager.getApplicationManager().postGlobalEvent(GUID, 0, 0, message, null);
        	}
        catch (IllegalArgumentException e) {
        	writeMessage("IllegalArgumentException: " + e.getMessage());
        	}
        catch (IllegalStateException e) {
        	writeMessage("IllegalStateException: " + e.getMessage());
        	}
		}

	// Turn off all indicators
	public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {

		// Enable Push
		if (guid==PUSH_ENABLE_GUID) {
			registerForService();
			}
		
		// Disable Push
		if (guid==PUSH_DISABLE_GUID) {
			deRegisterForService();
			}
		
		// Off event
		if (guid==PUSH_GUID) {
    		// Turn off the LED
            LED.setState( LED.STATE_OFF );
            
	        try {
	            // Set the indicator on
		        ApplicationIndicatorRegistry reg = ApplicationIndicatorRegistry.getInstance();
		        ApplicationIndicator appIndicator = reg.getApplicationIndicator();
	        	appIndicator.setVisible(false);

	        	Bitmap bm = Bitmap.getBitmapResource("uaicon.png");
				net.rim.blackberry.api.homescreen.HomeScreen.updateIcon(bm, 0);
	        	}
	        catch (IllegalStateException e) {}
			}
		}
}
