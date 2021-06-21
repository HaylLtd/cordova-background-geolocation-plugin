---
layout: default
title: Events
nav_order: 3
---

# Events

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

## Location event

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

## Activity event

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
