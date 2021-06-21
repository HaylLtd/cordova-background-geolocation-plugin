---
layout: default
title: Android Headless Task
nav_order: 7
---

# Android Headless Task

*Experimental*{: .bg-yellow-000}

A special task that gets executed when the app is terminated, but the plugin was configured to continue running in the background (option `stopOnTerminate: false`).
In this scenario the [Activity](https://developer.android.com/reference/android/app/Activity.html)
was killed by the system and all registered event listeners will not be triggered until the app is relaunched.

**Note:** Prefer configuration options `url` and `syncUrl` over headless task. Use it sparingly!

### Task event

| Parameter          | Type      | Description                                                            |
|--------------------|-----------|------------------------------------------------------------------------|
| `event.name`       | `String`  | Name of the event [ "location", "stationary", "activity" ]             |
| `event.params`     | `Object`  | Event parameters. See [Events](events)                               |

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
