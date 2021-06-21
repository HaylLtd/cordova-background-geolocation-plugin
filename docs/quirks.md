---
layout: default
title: Quirks
nav_order: 8
---

# Quirks

## iOS

On iOS the plugin will execute your registered `.on('location', callbackFn)` callback function. You may manually POST the received ```Geolocation``` to your server using standard XHR. However for long running tasks, you need
to wrap your code in `startTask` - `endTask` block.

### `stationaryRadius` (apply only for DISTANCE_FILTER_PROVIDER)

The plugin uses iOS Significant Changes API, and starts triggering ```callbackFn``` only when a cell-tower switch is detected (i.e. the device exits stationary radius). The function ```switchMode``` is provided to force the plugin to enter "BACKGROUND" stationary or "FOREGROUND" mode.

Plugin cannot detect the exact moment the device moves out of the stationary-radius.  In normal conditions, it can take as much as 3 city-blocks to 1/2 km before stationary-region exit is detected.

## Android

On Android devices it is recommended to have a notification in the drawer (option `startForeground:true`). This gives plugin location service higher priority, decreasing probability of OS killing it.

On Android devices, the location event callback will not consistently be called while the app is in the background, even with `startForeground:true`. If you need to post the location updates as they happen, you should use [HTTP Posting](http_posting).

### Custom ROMs

Plugin should work with custom ROMS at least DISTANCE_FILTER_PROVIDER. But ACTIVITY_PROVIDER provider depends on Google Play Services.
Usually ROMs don't include Google Play Services libraries. Strange bugs may occur, like no GPS locations (only from network and passive) and other. When posting issue report, please mention that you're using custom ROM.

### Multidex

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

### Android Permissions

Android 6.0 "Marshmallow" introduced a new permissions model where the user can turn on and off permissions as necessary. When user disallow location access permissions, error configure callback will be called with error code: 2.

### Notification icons

**Note:** Only available for API Level >=21.

To use custom notification icons, you need to put icons into *res/drawable* directory **of your app**. You can automate the process  as part of **after_platform_add** hook configured via [config.xml](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/config.xml). Check [config.xml](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/config.xml) and [scripts/res_android.js](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/scripts/res_android.js) of example app for reference.

If you only want a single large icon, set `notificationIconLarge` to null and include your icon's filename in the `notificationIconSmall` parameter.
