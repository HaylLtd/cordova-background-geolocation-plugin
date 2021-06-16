---
layout: default
nav_order: 1
---

# Introduction

Cross-platform geolocation for Cordova / PhoneGap with battery-saving "circular region monitoring" and "stop detection"
{: .fw-500 }

This plugin can be used for geolocation when the app is running in the foreground or background. It is more battery and data efficient than html5 geolocation or cordova-geolocation plugin. It can be used side by side with other geolocation providers (eg. html5 navigator.geolocation).

You can choose from two location location providers:

* **DISTANCE_FILTER_PROVIDER**
* **ACTIVITY_PROVIDER**
* **RAW_PROVIDER**

See [Which provider should I use?](./provider.md) for more information about providers.

This project is based on [@mauron85/cordova-plugin-background-geolocation](https://github.com/mauron85/cordova-plugin-background-geolocation), which in turn was based on the original [cordova-background-geolocation plugin](https://github.com/christocracy/cordova-plugin-background-geolocation) by [christocracy](https://github.com/christocracy). Hayl Ltd have taken on responsibility for hosting it and will be maintaining it and merging PRs from the community. If you have any fixes, features or updates that you would like included, please do raise a PR or issue on the GitHub repository.

We are also looking to maintainers to help with this, so that the project does not end up orphaned. If you are interested in helping out with maintaining the project, please see the [pinned discussion at GitHub](https://github.com/HaylLtd/cordova-background-geolocation-plugin/discussions/3).

The NPM package can be found at [cordova-background-geolocation-plugin](https://www.npmjs.com/package/cordova-background-geolocation-plugin).

## Installing the plugin

```bash
cordova plugin add cordova-background-geolocation-plugin
```

You may also want to change default iOS permission prompts and set specific google play version and android support library version for compatibility with other plugins.

**Note:** Always consult documentation of other plugins to figure out compatible versions.

```bash
cordova plugin add cordova-background-geolocation-plugin \
  --variable GOOGLE_PLAY_SERVICES_VERSION=11+ \
  --variable ANDROID_SUPPORT_LIBRARY_VERSION=26+ \
  --variable ALWAYS_USAGE_DESCRIPTION="App requires ..." \
  --variable MOTION_USAGE_DESCRIPTION="App requires motion detection"
```

**Note:** To apply changes, you must remove and reinstall plugin.

## Submitting issues

A properly filled issue report will significantly reduce number of follow up questions and decrease issue resolving time, so please do give as much information as you can in the issue report.
Most issues cannot be resolved without debug logs. Please try to isolate debug lines related to your issue.
Instructions for how to prepare debug logs can be found in section [Debugging](#debugging).
If you're reporting an app crash, debug logs might not contain all the necessary information about the cause of the crash.
In that case, also provide relevant parts of output of `adb logcat` command.

### Android background service issues

There are repeatedly reported issues with some android devices not working in the background. Check if your device model is on  [dontkillmyapp list](https://dontkillmyapp.com) before you report new issue. For more information check out [dontkillmyapp.com](https://dontkillmyapp.com/problem).

Another confusing fact about Android services is concept of foreground services. Foreground service in context of Android OS is different thing than background geolocation service of this plugin (they're related thought). **Plugin's background geolocation service** actually **becomes foreground service** when app is in the background. Confusing, right? :D

If service wants to continue to run in the background, it must "promote" itself to `foreground service`. Foreground services must have visible notification, which is the reason why you can't disable drawer notification.

The notification can only be disabled, when app is running in the foreground, by setting config option `startForeground: false` (this is the default option), but will always be visible in the background (if service was started).

Recommended reading: <https://developer.android.com/about/versions/oreo/background>
{: .bg-yellow-000}

## Compilation

### Compatibility

| Plugin version   | Cordova CLI       | Cordova Platform Android | Cordova Platform iOS |
|------------------|-------------------|--------------------------|----------------------|
| >1.0.0           | 8.0.0             | 8.0.0                    | 6.0.0                |

**Please note** that as of Cordova Android 8.0.0 icons are by default mipmap/ic_launcher  not mipmap/icon, so this plugin will have a build issue on < 8.0.0 Cordova Android builds, you will need to update the icons in AndroidManifest.xml to work on older versions.

### Android SDKs

You will need to ensure that you have installed the following items through the Android SDK Manager:

| Name                       | Version  |
|----------------------------|----------|
| Android SDK Tools          | >26.0.2  |
| Android SDK Platform-tools | >26.0.2  |
| Android SDK Build-tools    | >26.0.2  |
| Android Support Repository | >47      |
| Android Support Library    | >26.1.0  |
| Google Play Services       | >11.8.0  |
| Google Repository          | >58      |

Android is no longer supporting downloading support libraries through the SDK Manager.
The support libraries are now available through Google's Maven repository.

## Example

```js
function onDeviceReady() {
  BackgroundGeolocation.configure({
    locationProvider: BackgroundGeolocation.ACTIVITY_PROVIDER,
    desiredAccuracy: BackgroundGeolocation.HIGH_ACCURACY,
    stationaryRadius: 50,
    distanceFilter: 50,
    notificationTitle: 'Background tracking',
    notificationText: 'enabled',
    debug: true,
    interval: 10000,
    fastestInterval: 5000,
    activitiesInterval: 10000,
    url: 'http://192.168.81.15:3000/location',
    httpHeaders: {
      'X-FOO': 'bar'
    },
    // customize post properties
    postTemplate: {
      lat: '@latitude',
      lon: '@longitude',
      foo: 'bar' // you can also add your own properties
    }
  });

  BackgroundGeolocation.on('location', function(location) {
    // handle your locations here
    // to perform long running operation on iOS
    // you need to create background task
    BackgroundGeolocation.startTask(function(taskKey) {
      // execute long running task
      // eg. ajax post location
      // IMPORTANT: task has to be ended by endTask
      BackgroundGeolocation.endTask(taskKey);
    });
  });

  BackgroundGeolocation.on('stationary', function(stationaryLocation) {
    // handle stationary locations here
  });

  BackgroundGeolocation.on('error', function(error) {
    console.log('[ERROR] BackgroundGeolocation error:', error.code, error.message);
  });

  BackgroundGeolocation.on('start', function() {
    console.log('[INFO] BackgroundGeolocation service has been started');
  });

  BackgroundGeolocation.on('stop', function() {
    console.log('[INFO] BackgroundGeolocation service has been stopped');
  });

  BackgroundGeolocation.on('authorization', function(status) {
    console.log('[INFO] BackgroundGeolocation authorization status: ' + status);
    if (status !== BackgroundGeolocation.AUTHORIZED) {
      // we need to set delay or otherwise alert may not be shown
      setTimeout(function() {
        var showSettings = confirm('App requires location tracking permission. Would you like to open app settings?');
        if (showSettings) {
          return BackgroundGeolocation.showAppSettings();
        }
      }, 1000);
    }
  });

  BackgroundGeolocation.on('background', function() {
    console.log('[INFO] App is in background');
    // you can also reconfigure service (changes will be applied immediately)
    BackgroundGeolocation.configure({ debug: true });
  });

  BackgroundGeolocation.on('foreground', function() {
    console.log('[INFO] App is in foreground');
    BackgroundGeolocation.configure({ debug: false });
  });

  BackgroundGeolocation.on('abort_requested', function() {
    console.log('[INFO] Server responded with 285 Updates Not Required');

    // Here we can decide whether we want stop the updates or not.
    // If you've configured the server to return 285, then it means the server does not require further update.
    // So the normal thing to do here would be to `BackgroundGeolocation.stop()`.
    // But you might be counting on it to receive location updates in the UI, so you could just reconfigure and set `url` to null.
  });

  BackgroundGeolocation.on('http_authorization', () => {
    console.log('[INFO] App needs to authorize the http requests');
  });

  BackgroundGeolocation.checkStatus(function(status) {
    console.log('[INFO] BackgroundGeolocation service is running', status.isRunning);
    console.log('[INFO] BackgroundGeolocation services enabled', status.locationServicesEnabled);
    console.log('[INFO] BackgroundGeolocation auth status: ' + status.authorization);

    // you don't need to check status before start (this is just the example)
    if (!status.isRunning) {
      BackgroundGeolocation.start(); //triggers start on start event
    }
  });

  // you can also just start without checking for status
  // BackgroundGeolocation.start();

  // Don't forget to remove listeners at some point!
  // BackgroundGeolocation.removeAllListeners();
}

document.addEventListener('deviceready', onDeviceReady, false);
```

## Events

| Name                | Callback param         | Platform     | Provider*   | Description                                      |
|---------------------|------------------------|--------------|-------------|--------------------------------------------------|
| `location`          | `Location`             | all          | all         | on location update                               |
| `stationary`        | `Location`             | all          | DIS,ACT     | on device entered stationary mode                |
| `activity`          | `Activity`             | Android      | ACT         | on activity detection                            |
| `error`             | `{ code, message }`    | all          | all         | on plugin error                                  |
| `authorization`     | `status`               | all          | all         | on user toggle location service                  |
| `start`             |                        | all          | all         | geolocation has been started                     |
| `stop`              |                        | all          | all         | geolocation has been stopped                     |
| `foreground`        |                        | Android      | all         | app entered foreground state (visible)           |
| `background`        |                        | Android      | all         | app entered background state                     |
| `abort_requested`   |                        | all          | all         | server responded with "285 Updates Not Required" |
| `http_authorization`|                        | all          | all         | server responded with "401 Unauthorized"         |

### Location event

| Location parameter     | Type      | Description                                                            |
|------------------------|-----------|------------------------------------------------------------------------|
| `id`                   | `Number`  | ID of location as stored in DB (or null)                               |
| `provider`             | `String`  | gps, network, passive or fused                                         |
| `locationProvider`     | `Number`  | location provider                                                      |
| `time`                 | `Number`  | UTC time of this fix, in milliseconds since January 1, 1970.           |
| `latitude`             | `Number`  | Latitude, in degrees.                                                  |
| `longitude`            | `Number`  | Longitude, in degrees.                                                 |
| `accuracy`             | `Number`  | Estimated accuracy of this location, in meters.                        |
| `speed`                | `Number`  | Speed if it is available, in meters/second over ground.                |
| `altitude`             | `Number`  | Altitude if available, in meters above the WGS 84 reference ellipsoid. |
| `bearing`              | `Number`  | Bearing, in degrees.                                                   |
| `isFromMockProvider`   | `Boolean` | (android only) True if location was recorded by mock provider          |
| `mockLocationsEnabled` | `Boolean` | (android only) True if device has mock locations enabled               |

Locations parameters `isFromMockProvider` and `mockLocationsEnabled` are not posted to `url` or `syncUrl` by default.
Both can be requested via option `postTemplate`.

Note: Do not use location `id` as unique key in your database as ids will be reused when `option.maxLocations` is reached.

### Activity event

| Activity parameter | Type      | Description                                                            |
|--------------------|-----------|------------------------------------------------------------------------|
| `confidence`       | `Number`  | Percentage indicating the likelihood user is performing this activity. |
| `type`             | `String`  | "IN_VEHICLE", "ON_BICYCLE", "ON_FOOT", "RUNNING", "STILL",             |
|                    |           | "TILTING", "UNKNOWN", "WALKING"                                        |

Event listeners can registered with:

```javascript
const eventSubscription = BackgroundGeolocation.on('event', callbackFn);
```

And unregistered:

```javascript
eventSubscription.remove();
```

**Note:** Components should unregister all event listeners in `componentWillUnmount` method,
individually, or with `removeAllListeners`

## HTTP locations posting

All locations updates are recorded in the local db at all times. When the App is in foreground or background, in addition to storing location in local db,
the location callback function is triggered. The number of locations stored in db is limited by `option.maxLocations` and never exceeds this number.
Instead, old locations are replaced by new ones.

When `option.url` is defined, each location is also immediately posted to url defined by `option.url`.
If the post is successful, the location is marked as deleted in local db.

When `option.syncUrl` is defined, all locations that fail to post locations will be coalesced and sent in some time later in a one single batch.
Batch sync takes place only when the number of failed-to-post locations reaches `option.syncTreshold`.
Locations are sent only in single batch, when the number of locations reaches `option.syncTreshold`. (No individual locations will be sent)

The request body of posted locations is always an array, even when only one location is sent.

Warning: `option.maxLocations` has to be larger than `option.syncThreshold`. It's recommended to be 2x larger. In any other case the location syncing might not work properly.

## Custom post template

With `option.postTemplate` it is possible to specify which location properties should be posted to `option.url` or `option.syncUrl`. This can be useful to reduce
the number of bytes sent "over the wire".

All wanted location properties have to be prefixed with `@`. For all available properties check [Location event](#location-event).

Two forms are supported:

### jsonObject

```javascript
BackgroundGeolocation.configure({
  postTemplate: {
    lat: '@latitude',
    lon: '@longitude',
    foo: 'bar' // you can also add your own properties
  }
});
```

### jsonArray

```javascript
BackgroundGeolocation.configure({
  postTemplate: ['@latitude', '@longitude', 'foo', 'bar']
});
```

Note: Keep in mind that all locations (even a single one) will be sent as an array of object(s), when postTemplate is `jsonObject` and array of array(s) for `jsonArray`!

### Android Headless Task (Experimental)

A special task that gets executed when the app is terminated, but the plugin was configured to continue running in the background (option `stopOnTerminate: false`).
In this scenario the [Activity](https://developer.android.com/reference/android/app/Activity.html)
was killed by the system and all registered event listeners will not be triggered until the app is relaunched.

**Note:** Prefer configuration options `url` and `syncUrl` over headless task. Use it sparingly!

#### Task event

| Parameter          | Type      | Description                                                            |
|--------------------|-----------|------------------------------------------------------------------------|
| `event.name`       | `String`  | Name of the event [ "location", "stationary", "activity" ]             |
| `event.params`     | `Object`  | Event parameters. @see [Events](#events)                               |

Keep in mind that the callback function lives in an isolated scope. Variables from a higher scope cannot be referenced!

Following example requires [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) enabled backend server.

```javascript
BackgroundGeolocation.headlessTask(function(event) {
    if (event.name === 'location' ||
      event.name === 'stationary') {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://192.168.81.14:3000/headless');
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send(JSON.stringify(event.params));
    }

    return 'Processing event: ' + event.name; // will be logged
});
```

### Example of backend server

[Background-geolocation-server](https://github.com/mauron85/background-geolocation-server) is a backend server written in nodejs
with CORS - Cross-Origin Resource Sharing support.
There are instructions how to run it and simulate locations on Android, iOS Simulator and Genymotion.

## Quirks

### iOS

On iOS the plugin will execute your registered `.on('location', callbackFn)` callback function. You may manually POST the received ```Geolocation``` to your server using standard XHR. However for long running tasks, you need
to wrap your code in `startTask` - `endTask` block.

#### `stationaryRadius` (apply only for DISTANCE_FILTER_PROVIDER)

The plugin uses iOS Significant Changes API, and starts triggering ```callbackFn``` only when a cell-tower switch is detected (i.e. the device exits stationary radius). The function ```switchMode``` is provided to force the plugin to enter "BACKGROUND" stationary or "FOREGROUND" mode.

Plugin cannot detect the exact moment the device moves out of the stationary-radius.  In normal conditions, it can take as much as 3 city-blocks to 1/2 km before stationary-region exit is detected.

### Android

On Android devices it is recommended to have a notification in the drawer (option `startForeground:true`). This gives plugin location service higher priority, decreasing probability of OS killing it. Check [wiki](https://github.com/mauron85/cordova-plugin-background-geolocation/wiki/Android-implementation) for explanation.

#### Custom ROMs

Plugin should work with custom ROMS at least DISTANCE_FILTER_PROVIDER. But ACTIVITY_PROVIDER provider depends on Google Play Services.
Usually ROMs don't include Google Play Services libraries. Strange bugs may occur, like no GPS locations (only from network and passive) and other. When posting issue report, please mention that you're using custom ROM.

#### Multidex

**Note:** Following section was kindly copied from [phonegap-plugin-push](https://github.com/phonegap/phonegap-plugin-push/blob/master/docs/INSTALLATION.md#multidex). Visit link for resolving issue with facebook plugin.

If you have an issue compiling the app and you're getting an error similar to this (`com.android.dex.DexException: Multiple dex files define`):

```console
UNEXPECTED TOP-LEVEL EXCEPTION:
com.android.dex.DexException: Multiple dex files define Landroid/support/annotation/AnimRes;
  at com.android.dx.merge.DexMerger.readSortableTypes(DexMerger.java:596)
  at com.android.dx.merge.DexMerger.getSortedTypes(DexMerger.java:554)
  at com.android.dx.merge.DexMerger.mergeClassDefs(DexMerger.java:535)
  at com.android.dx.merge.DexMerger.mergeDexes(DexMerger.java:171)
  at com.android.dx.merge.DexMerger.merge(DexMerger.java:189)
  at com.android.dx.command.dexer.Main.mergeLibraryDexBuffers(Main.java:502)
  at com.android.dx.command.dexer.Main.runMonoDex(Main.java:334)
  at com.android.dx.command.dexer.Main.run(Main.java:277)
  at com.android.dx.command.dexer.Main.main(Main.java:245)
  at com.android.dx.command.Main.main(Main.java:106)
```

Then at least one other plugin you have installed is using an outdated way to declare dependencies such as `android-support` or `play-services-gcm`.
This causes gradle to fail, and you'll need to identify which plugin is causing it and request an update to the plugin author, so that it uses the proper way to declare dependencies for cordova.
See [this for the reference on the cordova plugin specification](https://cordova.apache.org/docs/en/5.4.0/plugin_ref/spec.html#link-18), it'll be usefull to mention it when creating an issue or requesting that plugin to be updated.

Common plugins to suffer from this outdated dependency management are plugins related to *facebook*, *google+*, *notifications*, *crosswalk* and *google maps*.

#### Android Permissions

Android 6.0 "Marshmallow" introduced a new permissions model where the user can turn on and off permissions as necessary. When user disallow location access permissions, error configure callback will be called with error code: 2.

#### Notification icons

**Note:** Only available for API Level >=21.

To use custom notification icons, you need to put icons into *res/drawable* directory **of your app**. You can automate the process  as part of **after_platform_add** hook configured via [config.xml](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/config.xml). Check [config.xml](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/config.xml) and [scripts/res_android.js](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/scripts/res_android.js) of example app for reference.

If you only want a single large icon, set `notificationIconLarge` to null and include your icon's filename in the `notificationIconSmall` parameter.

With Adobe® PhoneGap™ Build icons must be placed into ```locales/android/drawable``` dir at the root of your project. For more information go to [how-to-add-native-image-with-phonegap-build](http://stackoverflow.com/questions/30802589/how-to-add-native-image-with-phonegap-build/33221780#33221780).

### Intel XDK

Plugin will not work in XDK emulator ('Unimplemented API Emulation: BackgroundGeolocation.start' in emulator). But will work on real device.

## Debugging

See [DEBUGGING.md](/DEBUGGING.md)

## Geofencing

There is nice cordova plugin [cordova-plugin-geofence](https://github.com/cowbell/cordova-plugin-geofence), which does exactly that. Let's keep this plugin lightweight as much as possible.

## Changelog

See [CHANGES.md](/CHANGES.md)

## Licence

[Apache License](http://www.apache.org/licenses/LICENSE-2.0)

Copyright (c) 2013 Christopher Scott, Transistor Software

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
