/*
 According to apache license

 This is fork of christocracy cordova-plugin-background-geolocation plugin
 https://github.com/christocracy/cordova-plugin-background-geolocation

 Differences to original version:

 1. new method isLocationEnabled
 */

var exec = require('cordova/exec');
var channel = require('cordova/channel');
var radio = require('./radio');
var TAG = 'CDVBackgroundGeolocation';

var assert = function (condition, msgArray) {
  if (!condition) {
      throw new Error(msgArray.join('') || 'Assertion failed');
  }
}

var eventHandler = function (event) {
  radio(event.name).broadcast(event.payload);
};

var errorHandler = function (error) {
  radio('error').broadcast(error);
};

var unsubscribeAll = function (channels) {
  channels.forEach(function(channel) {
    var topic = radio(channel);
    var callbacks = [].concat.apply([], topic.channels[channel]); // flatten array
    topic.unsubscribe.apply(topic, callbacks);
  });
}

var execWithPromise = function (suceess, failure, method, data) {
  if (!suceess && !failure) {
    return new Promise(function (resolve, reject) {
      exec(resolve, reject, 'BackgroundGeolocation', method, data);    
    });
  }
  exec(suceess || function() {}, failure || function() {}, 'BackgroundGeolocation', method, data || []);
}

var BackgroundGeolocation = {
  events: [
    'location',
    'stationary',
    'activity',
    'start',
    'stop',
    'error',
    'authorization',
    'foreground',
    'background',
    'abort_requested',
    'http_authorization'
  ],

  DISTANCE_FILTER_PROVIDER: 0,
  ACTIVITY_PROVIDER: 1,
  RAW_PROVIDER: 2,

  BACKGROUND_MODE: 0,
  FOREGROUND_MODE: 1,

  NOT_AUTHORIZED: 0,
  AUTHORIZED: 1,
  AUTHORIZED_FOREGROUND: 2,

  HIGH_ACCURACY: 0,
  MEDIUM_ACCURACY: 100,
  LOW_ACCURACY: 1000,
  PASSIVE_ACCURACY: 10000,

  LOG_ERROR: 'ERROR',
  LOG_WARN: 'WARN',
  LOG_INFO: 'INFO',
  LOG_DEBUG: 'DEBUG',
  LOG_TRACE: 'TRACE',

  PERMISSION_DENIED: 1,
  LOCATION_UNAVAILABLE: 2,
  TIMEOUT: 3,

  configure: function (config, success, failure) {
    return execWithPromise(success,
      failure,
      'configure',
      [config]
    );
  },

  start: function () {
    return execWithPromise(null, null, 'start');
  },

  stop: function () {
    return execWithPromise(null, null, 'stop');
  },

  switchMode: function (mode, success, failure) {
    return execWithPromise(success,
      failure,
      'switchMode', [mode]);
  },

  getConfig: function (success, failure) {
    return execWithPromise(success,
      failure,
      'getConfig');
  },

  /**
   * Returns current stationaryLocation if available.  null if not
   */
  getStationaryLocation: function (success, failure) {
    return execWithPromise(success,
      failure,
      'getStationaryLocation');
  },

  showAppSettings: function () {
    return execWithPromise(null,
      null,
      'showAppSettings');
  },

  showLocationSettings: function () {
    return execWithPromise(null,
      null,
      'showLocationSettings');
  },

  getLocations: function (success, failure) {
    return execWithPromise(success,
      failure,
      'getLocations');
  },

  getValidLocations: function (success, failure) {
    return execWithPromise(success,
      failure,
      'getValidLocations');
  },

  getValidLocationsAndDelete: function (success, failure) {
    return execWithPromise(success, 
      failure,
      'getValidLocationsAndDelete');
  },

  deleteLocation: function (locationId, success, failure) {
    return execWithPromise(success,
      failure,
      'deleteLocation', [locationId]);
  },

  deleteAllLocations: function (success, failure) {
    return execWithPromise(success,
      failure,
      'deleteAllLocations');
  },

  getCurrentLocation: function(success, failure, options) {
    options = options || {};
    return execWithPromise(success,
      failure,
      'getCurrentLocation', [options.timeout, options.maximumAge, options.enableHighAccuracy]);
  },

  getLogEntries: function(limit, offset = 0, minLevel = "DEBUG", success, failure) {
    return execWithPromise(success,
      failure,
      'getLogEntries', [limit, offset, minLevel]);
  },

  checkStatus: function (success, failure) {
    return execWithPromise(success,
      failure,
      'checkStatus')
  },

  startTask: function (success, failure) {
    return execWithPromise(success,
      failure,
      'startTask');
  },

  endTask: function (taskKey, success, failure) {
    return execWithPromise(success,
      failure,
      'endTask', [taskKey]);
  },

  headlessTask: function (func, success, failure) {
    return execWithPromise(success,
      failure,
      'registerHeadlessTask', [func.toString()]);
  },

  forceSync: function (success, failure) {
    return execWithPromise(success,
      failure,
      'forceSync');
  },

  on: function (event, callbackFn) {
    assert(this.events.indexOf(event) > -1, [TAG, '#on unknown event "' + event + '"']);
    if (!callbackFn) {
      return radio(event);
    }
    radio(event).subscribe(callbackFn);
    return {
      remove: function () {
        radio(event).unsubscribe(callbackFn);
      }
    };
  },

  removeAllListeners: function (event) {
    if (!event) {
      unsubscribeAll(this.events);
      return void 0;
    }
    if (this.events.indexOf(event) < 0) {
      console.log('[WARN] ' + TAG + '#removeAllListeners for unknown event "' + event + '"');
      return void 0;
    }
    unsubscribeAll([event]);
  }
};

channel.deviceready.subscribe(function () {
  // register app global listeners
  exec(eventHandler,
    errorHandler,
    'BackgroundGeolocation',
    'addEventListener'
  );
});


module.exports = BackgroundGeolocation;
