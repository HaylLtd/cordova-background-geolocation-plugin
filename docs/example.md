---
layout: default
title: Example
nav_order: 4
---

# Example

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
