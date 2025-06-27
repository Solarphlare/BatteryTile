# BatteryTile

**BatteryTile** is a customizable Android Quick Settings tile that displays your battery's current state—including percentage, voltage, current, and temperature—directly in your notification shade. Instantly access full battery settings with a long-press, restoring a shortcut removed since Android 13.

---

<p align="center">
  <img src="https://github.com/CominAtYou/BatteryTile/assets/35669235/caac2c17-f5e3-4831-9c0d-5a5639231ad7" alt="BatteryTile screenshot 1" width="180"/>
  <img src="https://github.com/CominAtYou/BatteryTile/assets/35669235/b643d325-30a8-4fc2-82b7-bdb501dcf5fd" alt="BatteryTile screenshot 2" width="180"/>
  <img src="https://github.com/CominAtYou/BatteryTile/assets/35669235/9388a16d-e3c9-4788-a1b9-7a34965f98ad" alt="BatteryTile screenshot 3" width="180"/>
  <img src="https://github.com/CominAtYou/BatteryTile/assets/35669235/104ce02e-cf39-44ce-b72d-8689dab2b75b" alt="BatteryTile screenshot 4" width="180"/>
  <img src="https://github.com/CominAtYou/BatteryTile/assets/35669235/30ef866b-ea4a-4f96-b3a6-d3a7a80e7da3" alt="BatteryTile screenshot 5" width="180"/>
</p>

---

## Table of Contents

- [Features](#features)
- [Why BatteryTile?](#why-batterytile)
- [Compatibility](#compatibility)
- [Download](#download)
- [Screenshots](#screenshots)
- [FAQ](#faq)
- [Contributing](#contributing)

---

## Features

- **At-a-glance battery info:** View percentage, voltage, current, and temperature in your Quick Settings tile.
- **Customizable display:** Choose which stats to show and how the tile presents them.
- **Restores lost shortcuts:** Long-press opens full battery settings—just like classic Android.
- **Experimental Battery Saver Tile:** Optionally act as the Battery Saver tile for an Android 11-style experience (see [FAQ](#faq) for details).
- **Material You support:** Seamlessly blends with modern Android design.
- **No root required:** Advanced features can be enabled via a simple ADB command.

---

## Why BatteryTile?

In Android 12 and below, the Battery Saver tile's long-press opened full battery settings. Since Android 13, this shortcut was removed, limiting access to just Battery Saver options. BatteryTile restores this lost functionality and delivers flexible, real-time battery stats—directly in your notification shade.

---

## Compatibility

- **Official Support:** Android 10 (API 29) and above.
- **Optional:** Android 7.0 (API 24) and above if you remove references to [`Tile#setSubtitle`](https://developer.android.com/reference/android/service/quicksettings/Tile#setSubtitle(java.lang.CharSequence)) in the code.
- **No root required** for core features. Some advanced features require a one-time ADB command.

---

## Download

- **[Latest APK Release](https://github.com/CominAtYou/BatteryTile/releases/latest)**
- **F-Droid (IzzyOnDroid repo):**  
  [<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height="80">](https://apt.izzysoft.de/fdroid/index/apk/com.cominatyou.batterytile)
- **Google Play:** (English only, no Battery Saver tile feature)  
  [<img src="https://i.imgur.com/F9cBTKf.png" alt="Get it on Google Play" height="50">](https://play.google.com/store/apps/details?id=com.cominatyou.batterytile)

---

## Screenshots

| | | |
|:-------------------------:|:-------------------------:|:-------------------------:|
|![](https://github.com/CominAtYou/BatteryTile/assets/35669235/caac2c17-f5e3-4831-9c0d-5a5639231ad7) <br/>**Battery & Temp (wired charging)**|![](https://github.com/CominAtYou/BatteryTile/assets/35669235/b643d325-30a8-4fc2-82b7-bdb501dcf5fd) <br/>**Battery & Voltage (wireless charging)**|![](https://github.com/CominAtYou/BatteryTile/assets/35669235/9388a16d-e3c9-4788-a1b9-7a34965f98ad) <br/>**Temperature (discharging)**|
|![](https://github.com/CominAtYou/BatteryTile/assets/35669235/104ce02e-cf39-44ce-b72d-8689dab2b75b) <br/>**As Battery Saver tile**|![](https://github.com/CominAtYou/BatteryTile/assets/35669235/30ef866b-ea4a-4f96-b3a6-d3a7a80e7da3) <br/>**Default (charging, no customization)**||

---

## FAQ

### How do I customize the tile?
Open the BatteryTile app. Tap "Additional settings in the app" or "In-app notification settings" (the exact wording depends on your device) from the app's info page in system settings. Here, you can tailor which battery stats appear and how the tile looks.

### Does this app replace the existing Battery Saver tile?
No. BatteryTile adds a new, independent tile. You can use both tiles side by side.

### Why are the "Act as Battery Saver Tile" and "Tap to toggle Battery Saver" features experimental? Why do I need to use ADB to enable them?
Android does not provide an official API for third-party apps to toggle Battery Saver. Changing this setting requires `WRITE_SECURE_SETTINGS` permission, which you can only grant via ADB:
```sh
adb shell pm grant com.cominatyou.batterytile android.permission.WRITE_SECURE_SETTINGS
```
> **Note:** Toggling Battery Saver this way may temporarily break the default Battery Saver tile, settings toggles, and any automatic Battery Saver features until you disable it again with BatteryTile.

### Why is my language missing some formatters in its description?
Translations are community-provided. If your language is missing a formatter, it’s likely not yet translated in the description (but formatters still work if you type them). [Contribute a translation](https://github.com/CominAtYou/BatteryTile/pulls) if you can!

### My question isn’t listed here!
Open an [issue](https://github.com/CominAtYou/BatteryTile/issues) for bugs, questions, or feature requests.

---

## Contributing

Pull requests for translations, bugfixes, or new features are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

**BatteryTile — Modern, flexible battery info and shortcuts for Android.**
