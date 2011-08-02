package com.urbanairship.pushclient;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Custom dialog, a little prettier than the default Blackberry Alert
 */
public class UrbanAirshipDialog extends PopupScreen implements FieldChangeListener {
	
	private Bitmap chops						= Bitmap.getBitmapResource("uaicon.png");
	
	private String _message						= null;
	
	private VerticalFieldManager vfm 			= null;
	private LabelField messageField 			= null;
	private OKButton okButton 					= new OKButton(ButtonField.CONSUME_CLICK | FOCUSABLE | Field.FIELD_BOTTOM | Field.FIELD_HCENTER);
	
	/**
	 * Creates a custom dialog, a little prettier than the default Blackberry Alert
	 * 
	 * @param message Message to display in alert box
	 */
	public UrbanAirshipDialog(String message) {
		super(new VerticalFieldManager(FIELD_VCENTER), Manager.FIELD_VCENTER | Manager.FIELD_HCENTER);
		
		_message = message;

		vfm = new VerticalFieldManager(USE_ALL_WIDTH| Manager.FIELD_VCENTER | Manager.FIELD_HCENTER | Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		
        TableLayoutManager hfm = new TableLayoutManager(new int[]
                                                     		    {
 		        TableLayoutManager.FIXED_WIDTH,
                TableLayoutManager.SPLIT_REMAINING_WIDTH
                },
                 new int[] {chops.getWidth()+30, 0},
                 0,
                 Manager.USE_ALL_WIDTH | FIELD_HCENTER);
			
		BitmapField chopsField = new BitmapField(chops, Field.FIELD_LEFT | Field.FIELD_VCENTER);
		chopsField.setMargin(10, 10, 10, 20);
		
		messageField = new LabelField(_message, Field.FIELD_VCENTER) {
			protected void paint(Graphics g) {
				g.setColor(0xff8a8a8a);
				super.paint(g);
				}
			};
		messageField.setFont(Util.getStandardFont().derive(0,24));
		messageField.setMargin(10, 10, 10, 10);
		
		hfm.add(chopsField);
		hfm.add(messageField);
		
		vfm.add(hfm);
		
		okButton.setChangeListener(this);
		okButton.setMargin(10, 20, 10, 20);
		
		vfm.add(okButton);
		
		vfm.setMargin(10,10,10,0);
		add(vfm);
		}
	
	protected void paint(Graphics g) {
        g.setBackgroundColor(0xffffffff);
        g.clear();
		super.paint(g);
		}
	
	// To remove all theme-ing
	//protected void applyTheme()
    //{}
	
	protected boolean navigationClick(int status, int time) {
		close();
        return true;
    	}
    
    protected boolean keyChar(char character, int status, int time) {
    	if (Characters.ESCAPE == character) {
    		close();
    	    return true;
    		}
    	return super.keyChar(character, status, time);
    	}

	public void fieldChanged(Field field, int context) {
		close();
		}
}
