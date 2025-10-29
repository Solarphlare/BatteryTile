# BatteryTile
An Android Quick Settings tile that displays the current battery state and percentage, and a long-press action to open the battery settings.

## Features
- Customize the tile to display information about your battery, including voltage, current, percentage, and temperature.
- Brings back the "long press to open battery settings" feature removed in Android 12.
- [[**Experimental, Not Supported**](#why-are-the-act-as-battery-saver-tile-and-tap-to-toggle-battery-saver-features-not-officially-supported-why-do-i-need-to-use-adb-to-enable-them)] Have the tile act as the Battery Saver tile, for those who wish to have an experience as close as possible to Android 11 and below.


## Reasoning
The "Battery Saver" tile in Android 12 in below, when long-pressed, would open the battery settings page in Android Settings. With the introduction of Android 13,
it no longer presents the battery settings page. Instead, it now opens the Battery Saver settings, removing this very useful shortcut. This app aims to restore said functionality with a custom Quick Settings tile.

## Compatibility
This app is compatible with API 29+ devices (Android 10 or higher), as Quick Settings tile subtitles were added in that release.

Theoretically, compatibility could be extended to cover API 24+ as well (Android 7 and up) if references to
[Tile#setSubtitle](https://developer.android.com/reference/android/service/quicksettings/Tile#setSubtitle(java.lang.CharSequence))
were removed. However, this would defeat one of the purposes of the tile, but the option to do so is available to anyone who wishes to compile it on their own.

## Download
The latest version can be downloaded from [releases page](https://github.com/CominAtYou/BatteryTile/releases/latest).

Alternatively, you can get it on F-Droid via the IzzyOnDroid repo:

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png"
     alt="Get it on IzzyOnDroid"
     height="80">](https://apt.izzysoft.de/fdroid/index/apk/com.cominatyou.batterytile)

The app is also available on Google Play. Please note that the Google Play version is only localized for English and does not support the "act as Battery Saver tile" feature.

[<img src="https://i.imgur.com/F9cBTKf.png"
     alt="Get it on Google Play" height="50">](https://play.google.com/store/apps/details?id=com.cominatyou.batterytile)

## Screenshots
The tile can be customized in various ways, such as:
| | | |
|:-------------------------:|:-------------------------:|:-------------------------:|
|![](https://github.com/CominAtYou/BatteryTile/assets/35669235/caac2c17-f5e3-4831-9c0d-5a5639231ad7) Displaying battery level and temperature while wired charging |![](https://github.com/CominAtYou/BatteryTile/assets/35669235/b643d325-30a8-4fc2-82b7-bdb501dcf5fd) Displaying battery level and voltage while wirelessly charging | ![](https://github.com/CominAtYou/BatteryTile/assets/35669235/9388a16d-e3c9-4788-a1b9-7a34965f98ad) Displaying temperature while discharging |
| ![](https://github.com/CominAtYou/BatteryTile/assets/35669235/104ce02e-cf39-44ce-b72d-8689dab2b75b) Displaying as the Battery Saver tile | ![](https://github.com/CominAtYou/BatteryTile/assets/35669235/30ef866b-ea4a-4f96-b3a6-d3a7a80e7da3) Default when charging and no customizations are applied |


## Questions? Answers.
### How do I customize the tile?
You can customize the tile by tapping the "Additional settings in the app" or "In-app notification settings" button (or your device's equivalent) on the app's "App info" page within your Settings app, usually found under your Settings app's "Apps" category.

### Does this app replace the existing Battery Saver tile?
No - all it does is add a new, separate tile that you can use in place of the Battery Saver tile. You can still use the standard Battery Saver tile alongside the tile provided by this app.

### Why are the "Act as Battery Saver Tile" and "Tap to toggle Battery Saver" features not officially supported? Why do I need to use ADB to enable them?
Android has no official API to allow apps to control the state of the Battery Saver setting. It can only be done by manually toggling the setting through the Settings class, and changing the state of the Battery Saver setting requires the `WRITE_SECURE_SETTINGS` permission. This permission is only granted to specific system apps, such as your Settings app and other Android system components. The only other way to grant this permission is via ADB, which you must do before the app can be able to set the Battery Saver state.

As to why these features aren't officially supported, Android does not expect Battery Saver to be enabled or disabled via the Settings class. Because of this, these features can cause Android itself to no longer be able to control whether or not Battery Saver is on or off. This will mean that after using this app's tile to toggle Battery Saver, things like the standard Android Battery Saver tile won't work anymore, nor will the Battery Saver switches in your Settings app work, as well as any feature that automatically enables Battery Saver. However, tapping the tile from this app again to reverse the change will automatically fix this.

### Why is my language missing some formatters in its description?
The translations for this app are provided by the community. This means that when a new formatter is added, I'm not able to add a translation for any language other than English, meaning that the description text will be missing some formatters. They'll still work if you type them in — they're just not in the description text. If you speak a language that's missing formatters, feel free to open a pull request with any translations.


### My question isn't listed here!
Feel free to open an issue if you find a bug, or have any questions regarding this app.
