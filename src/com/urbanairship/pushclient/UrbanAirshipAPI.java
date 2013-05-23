package com.urbanairship.pushclient;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;

/**
 * Urban Airship API calls
 */
public class UrbanAirshipAPI {
	
	private static final String HTTPS_GO_URBANAIRSHIP_COM_API_DEVICE_PINS 	= "https://go.urbanairship.com/api/device_pins/";
	private static final String URBAN_AIRSHIP_REGISTER_PIN 					= "urbanAirshipRegisterPIN";
	private static final String URBAN_AIRSHIP_UNREGISTER_PIN 				= "urbanAirshipUnRegisterPIN";
	private static int m_httpStatus 										= 0;

	/**
	 * urbanAirshipAPICall REST-ful call to Urban Airship
	 * 
	 * @param httpVerb HTTP Verb (PUT, DELETE)
	 * @param methodName Calling method name
	 * @param payload (optional) JSON payload, or null
	 */
	static void urbanAirshipAPICall(String httpVerb, String methodName, byte[] payload) {
		
		String url 				= HTTPS_GO_URBANAIRSHIP_COM_API_DEVICE_PINS + (Integer.toString(DeviceInfo.getDeviceId(),16)).toUpperCase() + ";ConnectionType=mds-public;deviceside=false";		
		HttpConnection httpConn = null;
	    OutputStream out    	= null;
		
    	// Urban Airship Creds
    	String login 	= Keys.URBAN_AIRSHIP_APPKEY+":"+Keys.URBAN_AIRSHIP_APPSECRET;
    	byte[] encoded 	= null;
    	
        try {
			encoded = Base64OutputStream.encode(login.getBytes(), 0, login.length(), false, false);
		} catch (IOException e1) {
			e1.printStackTrace();
			}	
        
		try {
			ConnectionFactory connFact = new ConnectionFactory();
			ConnectionDescriptor connDesc;
			connDesc = connFact.getConnection(url);

			httpConn = (HttpConnection)connDesc.getConnection();
			httpConn.setRequestMethod(httpVerb);
			httpConn.setRequestProperty("Authorization", "Basic " + new String(encoded));
			
			if (payload!=null) {
				// Write out JSON payload
				httpConn.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_LENGTH, String.valueOf(payload.length));
				httpConn.setRequestProperty("Content-Type", "application/json");
        		out = httpConn.openOutputStream();
        		out.write(payload);
        		out.flush();
        		out.close();
				}
			
	        m_httpStatus 	= httpConn.getResponseCode();
   	
			if (m_httpStatus != 200 && m_httpStatus!=201 && m_httpStatus!=204) {
				String errorMessage = methodName + " Error: (" + m_httpStatus + ") Data: " + httpConn.getResponseMessage();
				((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage(errorMessage);
				Util.debugPrint(methodName, errorMessage);
				} 
			else {
				String okMessage = methodName + " (" + (Integer.toString(DeviceInfo.getDeviceId(),16)).toUpperCase() + ") OK (status: " + m_httpStatus + ")";
				((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage(okMessage);
				Util.debugPrint(methodName, okMessage);
				}
			}
		catch (NullPointerException e)
			{
			((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage("NullPointerException: " + e.getMessage());
			Util.debugPrint(methodName, "NullPointerException: " + e.getMessage());
			e.printStackTrace();
			}    
		catch (ConnectionClosedException e)
			{
			((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage("ConnectionClosedException: " + e.getMessage());
			Util.debugPrint(methodName, "ConnectionClosedException: " + e.getMessage());
			e.printStackTrace();
			}    
		catch (Exception e)
			{
			((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage("Exception: " + e.getMessage());
			Util.debugPrint(methodName, "Exception: " + e.getMessage());
			e.printStackTrace();
			}    
		finally {
			try {
				httpConn.close();
			} catch (IOException e) {
				((UrbanAirshipMain)UiApplication.getUiApplication()).setStatusMessage("IOException: " + e.getMessage());
				Util.debugPrint(methodName, "IOException: " + e.getMessage());
				e.printStackTrace();
				}
			}
		}
	
	/**
	 * Register Device PIN with Urban Airship, with an Alias.
	 * 
	 * @param alias device alias (e.g., "David's Device")
	 *
	 */
	static void urbanAirshipRegisterPINWithAlias(String alias) {
		
		// Build alias payload
		String jsonString 	= "{\"alias\":\"" + alias + "\"}";	
		byte[] json 		= jsonString.getBytes();
		
		// Call the API
		urbanAirshipAPICall("PUT", URBAN_AIRSHIP_REGISTER_PIN, json);
		}
	
	/**
	 * Register Device PIN with Urban Airship
	 */
	static void urbanAirshipRegisterPIN() {
		urbanAirshipRegisterPINWithAlias(null);
		}
	
	/**
	 * Un-Register Device PIN with Urban Airship
	 */
	static void urbanAirshipUnRegisterPIN() {
		
		// Call the API
		urbanAirshipAPICall("DELETE", URBAN_AIRSHIP_UNREGISTER_PIN, null);
		}

}

