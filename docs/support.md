---
layout: default
title: Support
nav_order: 9
---

# Support

A properly filled issue report will significantly reduce number of follow up questions and decrease issue resolving time, so please do give as much information as you can in the issue report.
Most issues cannot be resolved without debug logs. Please try to isolate debug lines related to your issue.
Instructions for how to prepare debug logs can be found in [Debugging](debugging).
If you're reporting an app crash, debug logs might not contain all the necessary information about the cause of the crash.
In that case, also provide relevant parts of output of `adb logcat` command.

[GitHub Issues](https://github.com/HaylLtd/cordova-background-geolocation-plugin/issues){: .btn .btn-purple }
[GitHub Discussions](https://github.com/HaylLtd/cordova-background-geolocation-plugin/discussions){: .btn .btn-blue }

## Android background service issues

There are repeatedly reported issues with some android devices not working in the background. Check if your device model is on  [dontkillmyapp list](https://dontkillmyapp.com) before you report new issue. For more information check out [dontkillmyapp.com](https://dontkillmyapp.com/problem).

Another confusing fact about Android services is concept of foreground services. Foreground service in context of Android OS is different thing than background geolocation service of this plugin (they're related thought). **Plugin's background geolocation service** actually **becomes foreground service** when app is in the background. Confusing, right? :D

If service wants to continue to run in the background, it must "promote" itself to `foreground service`. Foreground services must have visible notification, which is the reason why you can't disable drawer notification.

The notification can only be disabled, when app is running in the foreground, by setting config option `startForeground: false` (this is the default option), but will always be visible in the background (if service was started).

Recommended reading: <https://developer.android.com/about/versions/oreo/background>
{: .bg-yellow-000}
