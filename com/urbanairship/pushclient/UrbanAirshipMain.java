package com.urbanairship.pushclient;

import java.io.IOException;
import javax.microedition.io.StreamConnection;
import net.rim.blackberry.api.push.PushApplication;
import net.rim.blackberry.api.push.PushApplicationStatus;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;

public final class UrbanAirshipMain extends UiApplication implements GlobalEventListener, PushApplication {
	
	private static final String URBAN_AIRSHIP_MAIN 	= "UrbanAirshipMain";
	private final UrbanAirshipMain	_uiApp			= (UrbanAirshipMain)UiApplication.getUiApplication();
	private static final long GUID					= 0x30120912ea356c9cL;
	private static final long PUSH_GUID				= 0x8aba2a7ecd1ac66cL;

	private PushConnector pc						= null;
	
	private UAHomeScreen _hs						= null;

	private boolean acceptsForeground				= false;

	/**
	 * Starts a background process on device restart; otherwise, starts the event dispatcher and shows the home screen
	 * 
	 * @param args "autostartup" in args[0] kicks off background process.
	 */
	public static void main(String[] args) {
		
    	UrbanAirshipMain nd = new UrbanAirshipMain();
    	
        if( args.length > 0 && args[ 0 ].equals( "autostartup" ) )
    		{        	
        	// Register push application
        	nd.registerPushApplication();      
        	// Create background process on device restart, no UI
        	nd.enterEventDispatcher(); 
    		}
        else
    		{
        	// Display the User Interface on foreground starts
        	nd.showGUI();
    		}
   		}
	
	/**
	 * Shows the user interface and instantiates the event dispatcher.
	 */
    public void showGUI() {
    	// So we can see the app.
    	acceptsForeground = true;
    	_uiApp.requestForeground();
    	
    	String model = DeviceInfo.getDeviceName();
        Util.debugPrint(URBAN_AIRSHIP_MAIN, "Model: " + model);
        
        // So we can receive alerts from the notification thread
        addGlobalEventListener(this);

        // Register our PIN with Urban Airship
		Thread t0 = new Thread() {
			public void run() {
				if (UrbanAirshipStore.isPushEnabled().booleanValue()==true) {
					// Register our Device PIN with Urban Airship (without Alias)
					// UrbanAirshipAPI.urbanAirshipRegisterPIN();
					// Register our Device PIN with Urban Airship (with Alias)
					UrbanAirshipAPI.urbanAirshipRegisterPINWithAlias("My Device");
					}
				else {
					// Un-Register our Device PIN with Urban Airship 
					UrbanAirshipAPI.urbanAirshipUnRegisterPIN();
					}
		        }
			};
		t0.start();
		
		_uiApp._hs = new UAHomeScreen();
		UiApplication.getUiApplication().pushScreen(_uiApp._hs);
		
        // Prompt for app permissions
        promptPermissions();
		
		// Enter event dispatcher
        enterEventDispatcher();
		}
	
    /**
     * Handle our inbound notifications
     */
	void handleNotifications() {
		
		Runnable r = new Runnable() {
			public void run() {
        		String notification = UrbanAirshipStore.getNotification();
        		if (!notification.equalsIgnoreCase("")) {
        			UrbanAirshipDialog uad = new UrbanAirshipDialog(notification);
        			try {
        				_uiApp.pushModalScreen(uad);
        				}
        			catch (IllegalStateException e) {}
        			UrbanAirshipStore.setNotification("");
        			}
        		// Send event notification to turn off indicators
                ApplicationManager.getApplicationManager().postGlobalEvent(PUSH_GUID);
        		}
			};
		_uiApp.invokeLater(r);
		}
   
	/**
	 * Used to hide background process from application switcher.
	 */
    protected boolean acceptsForeground() {
    	return acceptsForeground;
    	}
  
   /**
   * Prompt for app permissions
   */
    private void promptPermissions() {
        ApplicationPermissionsManager apm 	= ApplicationPermissionsManager.getInstance();
        ApplicationPermissions ap 			= apm.getApplicationPermissions();
        
    	boolean permissionsOK = false;
        if (ap.getPermission(ApplicationPermissions.PERMISSION_FILE_API) ==
            ApplicationPermissions.VALUE_ALLOW
            &&
            ap.getPermission(ApplicationPermissions.PERMISSION_INTERNET) ==
            ApplicationPermissions.VALUE_ALLOW
            &&
            ap.getPermission(ApplicationPermissions.PERMISSION_CROSS_APPLICATION_COMMUNICATION) ==
            ApplicationPermissions.VALUE_ALLOW
            &&
            ap.getPermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION) ==
            ApplicationPermissions.VALUE_ALLOW
            &&
            ap.getPermission(ApplicationPermissions.PERMISSION_WIFI) ==
            ApplicationPermissions.VALUE_ALLOW) {
        	permissionsOK = true;
        } else {
            ap.addPermission(ApplicationPermissions.PERMISSION_FILE_API);
            ap.addPermission(ApplicationPermissions.PERMISSION_INTERNET);
            ap.addPermission(ApplicationPermissions.PERMISSION_WIFI);
            ap.addPermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION);
            ap.addPermission(ApplicationPermissions.PERMISSION_CROSS_APPLICATION_COMMUNICATION);
            
            permissionsOK = apm.invokePermissionsRequest(ap);
        	} 
        
        if (!permissionsOK) {
        	synchronized (getEventLock()) {
        		invokeLater(new Runnable() {
        		public void run() {
        			UrbanAirshipDialog wrd = new UrbanAirshipDialog("Insufficient Permissions to run Urban Airship Push Client... the application will now exit.");
        			try {
        				_uiApp.pushModalScreen(wrd);
        				}
        			catch (IllegalStateException e) {}
    				requestForeground();
        			}
        		});}
        	System.exit(0);
        	}
        else {
            }
        }
    
    /**
     * setStatusMessage Sets the status message on the Home Screen.
     * 
     * @param message the message to be displayed in the push status area.
     */
    public void setStatusMessage(final String message) {
		try {
	        Runnable t2 = new Runnable() {
	        	public void run() {
	        		if (_hs!=null && message!=null) {
	        			_uiApp._hs.lfPushStatus.setText(message);
	        			}
	        		}
	        	};
	        	
	        _uiApp.invokeLater(t2);
			}
		catch (NullPointerException ex) {}
		catch (IllegalStateException ex) {}
    }
    
    /**
     * Register (Deregister) our app with the RIM Push Service
     */
    public void registerPushApplication() {
    	
    	if (pc==null) {
    		pc = new PushConnector();	
    		}
		
		if (UrbanAirshipStore.isPushEnabled().booleanValue()==true) {
			// Push is enabled... register with RIM
			// pc.registerForService(); 
	        ApplicationManager.getApplicationManager().postGlobalEvent(PushConnector.PUSH_ENABLE_GUID);
			}
		else {
			// pc.deRegisterForService();
	        ApplicationManager.getApplicationManager().postGlobalEvent(PushConnector.PUSH_DISABLE_GUID);
			} 
		}

	// To turn on/off indicator
	public void eventOccurred(long guid, int data0, int data1, final Object notification, Object object1) {
		// On Event
		if (guid==GUID) {
	        Runnable r = new Runnable() {
	        	public void run() {
	                
	    	        if (notification!=null) {
	    	        	UrbanAirshipDialog uad = new UrbanAirshipDialog((String)notification);
	    	        	try {
	    	        		_uiApp.pushModalScreen(uad);
	    	        		}
	    	        	catch (IllegalStateException e) {}
	    	        	// Clear the notification in the data store
	        			UrbanAirshipStore.setNotification("");
        				}
	    	        
	    	        // Send event notification to turn off indicators
	    	        ApplicationManager.getApplicationManager().postGlobalEvent(PUSH_GUID);
	        		}
	        	};
	        _uiApp.invokeLater(r);
			}
		}
	
	/**
	 * onMessage handle inbound push notifications
	 * 
	 * @param stream inbound PushInputStream
	 * @param conn inbound StreamConnection
	 */
	public void onMessage(final PushInputStream stream, final StreamConnection conn) {
    	
    	// Buffer for reading
		final byte[] buffer = new byte[15360];
		
		Thread t0 = new Thread() {
			public void run() {
			try {
			    // Temp storage
			    int size = stream.read( buffer );
			    byte[] binaryData = new byte[size];
			    System.arraycopy( buffer, 0, binaryData, 0, size );
			    
			    // Close
			    stream.accept();
			    stream.close();
			    conn.close();
			    
			    // Get the String
			    pc.handleMessage( new String(binaryData));
				}
			catch (IOException e1) {}
			catch (Exception e1) {}
			}
		};
		t0.start();
	}
	
	/**
	 * onStatusChange Called when Push Status changes
	 * 
	 * @param status changed push state.
	 */
	public void onStatusChange(PushApplicationStatus status) {
		switch (status.getStatus()) {
			case PushApplicationStatus.STATUS_ACTIVE:
				setStatusMessage("Application is actively listening.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "Application is actively listening.");
				break;
			case PushApplicationStatus.STATUS_FAILED:
				setStatusMessage("Subscription status failed.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "Subscription status failed.");
				break;
			case PushApplicationStatus.STATUS_NOT_REGISTERED:
				setStatusMessage("Application didn't register for push messages.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "Application didn't register for push messages.");
				break;
			case PushApplicationStatus.STATUS_PENDING:
				setStatusMessage("Push communications requested but is not confirmed yet.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "Push communications requested but is not confirmed yet.");
				break;
			case PushApplicationStatus.REASON_NETWORK_ERROR:
				setStatusMessage("Communication failed due to network error.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "Communication failed due to network error.");
				break;
			case PushApplicationStatus.REASON_SIM_CHANGE:
				setStatusMessage("SIM card change.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "SIM card change.");
				break;
			case PushApplicationStatus.REASON_API_CALL:
				setStatusMessage("Status change was initiated by API call.");
				Util.debugPrint(URBAN_AIRSHIP_MAIN, "Status change was initiated by API call.");
				break;
			}
		}
}
