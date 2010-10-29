package com.urbanairship.pushclient;

import net.rim.device.api.collection.util.LongHashtableCollection;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

/**
 * Application data store for the Urban Airship Push Client app.
 */
public class UrbanAirshipStore {
	
	   private static final long KEY_NOTIFICATION = 1;
	   private static final long KEY_PUSH_ENABLED = 2;
	   
	   private static PersistentObject 	_store;
	   private static UrbanAirshipStore _instance;
	   private LongHashtableCollection  _settings;
	   
	   private static final long GUID	= 0x30120912ea356c9cL;
	   
	   public UrbanAirshipStore() {
	        _store = PersistentStore.getPersistentObject(GUID);
	   		}

	   /**
	    * Retrieve notification.
	    */
	    public static String getNotification() {
	       String notification = (String)getInstance().get(KEY_NOTIFICATION);
	       if (null == notification || notification.length() == 0) {
	    	   	notification =  ""; 
	    	   	setNotification(notification);
	       		}
	       return notification;
	    	}
	    
	    /**
	     * Save notification.
	     * 
	     * @param notification The notification message to save.
	     */
	    public static void setNotification(String notification) {
		       	getInstance().set(KEY_NOTIFICATION, notification);
		    	} 
	    
		/**
		* Is Push Enabled? true or false
		*/
		public static Boolean isPushEnabled() {
			Boolean pushEnabled = (Boolean)getInstance().get(KEY_PUSH_ENABLED);
		    if (null == pushEnabled) {
		    	// Enabled by default
		    	pushEnabled =  new Boolean(true); 
		    	setPushEnabled(pushEnabled);
		       	}
		    return pushEnabled;
		    }
	    
	    /**
	     * Set Push Enabled.
	     * 
	     * @param pushEnabled boolean true or false
	     */
	    public static void setPushEnabled(Boolean pushEnabled) {
	       	getInstance().set(KEY_PUSH_ENABLED, pushEnabled);
	    	} 
	   
	   private static UrbanAirshipStore getInstance() {
	        if (null == _instance) {
	            _instance = new UrbanAirshipStore();
	        	}
	        return _instance; 
	    	}
	    
	    private void set(long key, Object value) {
	        synchronized(_store) {
	            _settings = (LongHashtableCollection)_store.getContents();
	            if (null == _settings) {
	                _settings = new LongHashtableCollection();
	            }
	            _settings.put(key,value);   
	            _store.setContents(_settings);
	            _store.commit();
	        }
	    }    
	    
	    private Object get(long key) {
	        synchronized(_store) {
	            _settings = (LongHashtableCollection)_store.getContents();
	            if (null != _settings && _settings.size() != 0) {
	                 return _settings.get(key);
	            } else {
	                 return null;
	            }
	        }
	    }

}
