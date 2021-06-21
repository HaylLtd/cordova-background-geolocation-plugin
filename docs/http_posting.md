---
layout: default
title: HTTP Location Posting
nav_order: 5
---

# HTTP Location Posting

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
{: .bg-yellow-000}

## Custom post template

With `option.postTemplate` it is possible to specify which location properties should be posted to `option.url` or `option.syncUrl`. This can be useful to reduce
the number of bytes sent "over the wire".

All wanted location properties have to be prefixed with `@`. For all available properties check [Location event](events#location-event).

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

**Note:** Keep in mind that all locations (even a single one) will be sent as an array of object(s), when postTemplate is `jsonObject` and array of array(s) for `jsonArray`!
