WMB
===

Inspired by Wheresmyb.us and developed primarily during the OSU Bus App Hackathon.

[Play Store Listing](https://play.google.com/store/apps/details?id=com.jmstudios.corvallistransit)

How to Build
============
Disclaimer: this app was built entirely in Android Studio.  If you prefer command-line configuration, you're on your own (but you probably don't need much help in setting this up anyways =) ).

1. Get Android Studio
2. If you're unfamiliar with Gradle in an Android Studio setting, read [this](http://developer.android.com/sdk/installing/studio-build.html)
3. Ensure you have APIs from 15 and onward, and updated SDK, Platform, and Build tools.  Get these from the SDK manager.  It's recommended that you download the sources and documentation too - this helps tremendously when figuring out what a particular method is doing.
4. Download the Google Play Services and Google Repository packages from the SDK Manager.  These are required for Maps.
5. Download the Android Support Repository and Android Support Library packages.
6. Request the debug.keystore file - you will not be able to render maps on your device unless you use the debug.keystore file the Maps API key is registered with.


Features and Screenshots:
=========================

- Real-time ETA for stops on a per-route basis
- Ability to set reminders to catch a bus from 5m to 20m intervals
- Pull-to-refresh updating on a per-route basis
- Map View for each route
- Map View for an individual stop
- (Planned) Walking directions, via Google Maps, to a particular stop

Arrivals View:

![alt tag](http://i.imgur.com/DFKnT7y.png?1)

Route Map View (Zoomed out, clustered stops):

![alt tag](http://i.imgur.com/Z96DrrR.png?1)

Route Map View (Stop Marker pressed, zoomed in more):

![alt tag](http://i.imgur.com/q2Q1aGZ.png?1)

Reminders View:

![alt tag](http://i.imgur.com/uNsoxqT.png?1)

Nav Drawer:

![alt tag](http://i.imgur.com/MmcFFNv.png?1)
