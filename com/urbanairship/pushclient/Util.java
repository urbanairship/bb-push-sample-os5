package com.urbanairship.pushclient;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;

/**
 * Utility methods.
 */
public class Util {
	
	/**
	 * Debug printing to the console
	 * 
	 * @param className the simple class name of the calling method.
	 * @param msg the message to send to the console.
	 */
	static public void debugPrint(String className, String msg) {
		System.out.println("\n#################");
		System.out.println(className + ": " + msg);
		System.out.println("#################\n");
		}
	
	/**
	 * Get the standard font
	 */
	public static Font getStandardFont() {
	
		Font retVal = Font.getDefault();
		
		try {
			FontFamily ff = null;
			ff = FontFamily.forName("BBAlpha Sans");
			retVal = ff.getFont(Font.PLAIN, 9);
			}
		catch (ClassNotFoundException e) {
			Util.debugPrint("Util", "" + e.getMessage());
			}
	
		return retVal;
		}
}
