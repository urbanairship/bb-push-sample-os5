Urban Airship Push Client for BB OS 5+
======================================

Urban Airship Push Client for BB OS 5+ is a sample application designed to
quickly get you up and running with Urban Airship Push Notifications, and to
provide an example for how you might integrate push inside of your RIM
Blackberry OS 5 or 6 application.

Requirements
------------

BlackBerry JDE 5+

Notable Components
------------------

Keys.java
   The class where all of your application settings are stored.  These include:

   - Blackberry Application ID (obtained from RIM)
   - Blackberry Push URL (Obtained from RIM); in development/evaluation mode
     this is http://pushapi.eval.blackberry.com, in production it usually
     http://pushapi.na.blackberry.com
   - Blackberry Push Port (Obtained from RIM)
   - Urban Airship Application Key (Obtained from http://go.urbanairship.com)
   - Urban Airship Application Secret (Obtained from http://go.urbanairship.com)
	
PushConnector.java
   The helper class that sets up listening to events and notifications in the
   background.

UrbanAirshipAPI.java
   The API class that handles communication between UA and your app.

   - urbanAirshipRegisterPIN: registers your device PIN with Urban Airship for
     notifications.
   - urbanAirshipUnRegisterPIN: un-registers your device PIN with Urban
     Airship, marking that the PIN should not receive any other notifications
     until registered again.
   - urbanAirshipRegisterPINWithAlias: allows you to register your device PIN
     and associate an Alias with the device.

UrbanAirshipMain.java
   The main application entry point. The application is designed to have an
   alternate entry point for starting background listening for notifications on
   device reset.

OKButton.java, TabletLayoutManager.java, UAHomeScreen.java, UrbanAirshipDialog.java, Util.java
   Classes used for presenting UI for the sample app.

UrbanAirshipStore
   Singleton class for providing persistent application storage, for keeping
   track of whether push services are enabled or disabled for the device.

Usage
-----

- Import the project into Eclipse or your favorite IDE.
- Open the Keys.java file and enter your application's unique RIM / UA
  information.
- Compile the app.
- Using the application dashboard at go.urbanairship.com, send test messages to
  your application.


Integrating it into your application
------------------------------------

- Copy the Keys.java module to your app.
- Copy the UrbanAirshipAPI.java module to your app.
- Copy the Util.java module to your app.
- Copy the PushConnector.java module to your app.
- Follow the model of the code in UrbanAirshipMain for implementing
  GlobalEventListener.

