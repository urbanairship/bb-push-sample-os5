package com.urbanairship.pushclient;

import net.rim.device.api.ui.Graphics;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class UAHomeScreen extends MainScreen {
	
	private int _statusColor = UrbanAirshipStore.isPushEnabled().booleanValue()?0xff008000:0xff800000;
	
	private final UrbanAirshipMain	_uiApp	= (UrbanAirshipMain)UiApplication.getUiApplication();
	public final LabelField lfPushStatus	= new LabelField("", Field.FIELD_BOTTOM | Field.FIELD_LEFT) {
		protected void paint(Graphics g) {
			g.setColor(_statusColor);
			super.paint(g);
			}
		};
	
	/**
	 * Create the home screen
	 */
	public UAHomeScreen() {
		super(NO_VERTICAL_SCROLL);
		
		VerticalFieldManager vfm0 = new VerticalFieldManager(Manager.FIELD_HCENTER | Manager.FIELD_VCENTER | Manager.USE_ALL_HEIGHT | Manager.USE_ALL_WIDTH);

		// Set underlying screen background
		vfm0.setBackground(BackgroundFactory.createBitmapBackground(Bitmap.getBitmapResource("img_airship.png"),Background.POSITION_X_CENTER,Background.POSITION_Y_CENTER,Background.REPEAT_SCALE_TO_FIT));
		
		// Mask to make the background look like a "watermark"
		VerticalFieldManager vfm = new VerticalFieldManager(Manager.FIELD_HCENTER | Manager.FIELD_VCENTER | Manager.USE_ALL_HEIGHT | Manager.USE_ALL_WIDTH);
		Background background = BackgroundFactory.createSolidTransparentBackground(Color.WHITE, 160);
		vfm.setBackground(background);
		
		// Add masking over the bottom image
		vfm0.add(vfm);
		
		// Header bitmap
		Bitmap bm = Bitmap.getBitmapResource("uaHeader.png");
		BitmapField bmf = new BitmapField(bm, Field.FIELD_HCENTER | Field.FIELD_TOP);
		bmf.setMargin(20,0,40,0);
		
		vfm.add(bmf);
		
		// UA App ID Display
        TableLayoutManager hfm1 = new TableLayoutManager(new int[]
                                                     		    {
 		        TableLayoutManager.FIXED_WIDTH,
                TableLayoutManager.SPLIT_REMAINING_WIDTH
                },
                 new int[] {170, 0},
                 0,
                 Manager.USE_ALL_WIDTH | FIELD_HCENTER);
		
        String appIdLabel  = "App ID: ";
		LabelField lfAppIdLabel = new LabelField(appIdLabel, Field.FIELD_BOTTOM | Field.FIELD_RIGHT);
		lfAppIdLabel.setFont(Util.getStandardFont().derive(Font.BOLD, 24));
		
		String appIdString = Keys.URBAN_AIRSHIP_APPKEY;
		LabelField lfAppId = new LabelField(appIdString, Field.FIELD_BOTTOM | Field.FIELD_LEFT);
		lfAppId.setFont(Util.getStandardFont().derive(Font.PLAIN, 24));
		
		hfm1.add(lfAppIdLabel);
		hfm1.add(lfAppId);		
		vfm.add(hfm1);
		
		// Device PIN Display
        TableLayoutManager hfm2 = new TableLayoutManager(new int[]
                                                      		    {
  		        TableLayoutManager.FIXED_WIDTH,
                 TableLayoutManager.SPLIT_REMAINING_WIDTH
                 },
                  new int[] {170, 0},
                  0,
                  Manager.USE_ALL_WIDTH | FIELD_HCENTER);
		
        String pinLabel  = "Device PIN: ";
		LabelField lfPinLabel = new LabelField(pinLabel, Field.FIELD_BOTTOM | Field.FIELD_RIGHT);
		lfPinLabel.setFont(Util.getStandardFont().derive(Font.BOLD, 24));
		
		String pinString =  Integer.toString(DeviceInfo.getDeviceId(),16).toUpperCase();
		LabelField lfPin = new LabelField(pinString, Field.FIELD_BOTTOM | Field.FIELD_LEFT);
		lfPin.setFont(Util.getStandardFont().derive(Font.PLAIN, 24));
		
		hfm2.add(lfPinLabel);
		hfm2.add(lfPin);
		vfm.add(hfm2);
		
		// Add the Push Status Display
        TableLayoutManager hfm3 = new TableLayoutManager(new int[]
                                                       		    {
   		        TableLayoutManager.FIXED_WIDTH,
                  TableLayoutManager.SPLIT_REMAINING_WIDTH
                  },
                   new int[] {170, 0},
                   0,
                   Manager.USE_ALL_WIDTH | FIELD_HCENTER);
        
        LabelField lfStatusLabel = new LabelField("Push Status: ", Field.FIELD_TOP | Field.FIELD_RIGHT);
        lfStatusLabel.setFont(Util.getStandardFont().derive(Font.BOLD, 24));
        hfm3.add(lfStatusLabel);
        
		hfm3.add(lfPushStatus);		
		lfPushStatus.setFont(Util.getStandardFont().derive(Font.PLAIN, 24));
		lfPushStatus.setMargin(0,10,0,0);
		
		vfm.add(hfm3);
		
		// Add the vertical field manager to the screen
		add(vfm0);
		
		// Handle any inbound notifications
        _uiApp.handleNotifications();
		}
	
	/**
	 * makeMenu
	 */
	protected void makeMenu(Menu menu, int instance) {
        menu.add(MenuItem.separator(299));
        if (UrbanAirshipStore.isPushEnabled().booleanValue()==true) {
        	menu.add(_disablePushItem);
            }
        else {
        	menu.add(_enablePushItem);
            }
        menu.add(MenuItem.separator(301));
        super.makeMenu(menu, instance);
    	}
	
	/**
	 * Enable push notifications, with UA and RIM.
	 */
    private MenuItem _enablePushItem = new MenuItem("Enable Push" , 300, 300) 
    {
        public void run() {
        	// Set Push State to true
        	UrbanAirshipStore.setPushEnabled(new Boolean(true));
        	
        	lfPushStatus.setText("Enabling...");
        	_statusColor = 0xff008000;
        	
            // Register our PIN with Urban Airship
    		Thread t0 = new Thread() {
    			public void run() {
    	        	// Register / De-Register Push with RIM
    	        	_uiApp.registerPushApplication();        	
    	        	
    				// Register our Device PIN with Urban Airship (without Alias)
    				UrbanAirshipAPI.urbanAirshipRegisterPIN();
    				// Register our Device PIN with Urban Airship (with Alias)
    				// UrbanAirshipAPI.urbanAirshipRegisterPINWithAlias("My Device");
    		        }
    			};
    		t0.start();
        	}
    };
	
    /**
     * Disable push notifications, with UA and RIM.
     */
    private MenuItem _disablePushItem = new MenuItem("Disable Push" , 300, 300) 
    {
        public void run() {
        	// Set Push State to false
        	UrbanAirshipStore.setPushEnabled(new Boolean(false));
        	
        	lfPushStatus.setText("Disabling...");
        	_statusColor = 0xff800000;
                    	
            // Register our PIN with Urban Airship
    		Thread t0 = new Thread() {
    			public void run() {
    	        	// Register / De-Register Push with RIM
    	        	_uiApp.registerPushApplication();        	
    	        	
    				// Un-Register our Device PIN with Urban Airship
    				UrbanAirshipAPI.urbanAirshipUnRegisterPIN();
    				// Register our Device PIN with Urban Airship (with Alias)
    				// UrbanAirshipAPI.urbanAirshipRegisterPINWithAlias("My Device");
    		        }
    			};
    		t0.start();
        	}
    	};
       	
    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    	}
    
    protected boolean keyChar(char character, int status, int time) {    	
    	if (Characters.ESCAPE == character) {
    		System.exit(0);
    	    return true;
    		}
    	return super.keyChar(character, status, time);
    	}
	
}
