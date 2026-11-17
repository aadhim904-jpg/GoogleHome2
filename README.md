# GoogleHome2

A fresh Android TV launcher starter project. It is separate from the earlier TvHome project.

## Build
- JDK 17
- Android SDK platform 35 and build-tools 35.0.0
- Gradle 8.7

Open this folder in Android Studio and sync Gradle, or upload the project to a new GitHub repository and run the included GitHub Actions workflow manually.

## Current starter features
- Google TV-inspired dark home screen
- App discovery and launching (Leanback and standard launcher activities)
- Separate settings page with shortcuts to Android settings screens
- Source shortcut attempts to open the TV input settings screen

## Important TV limitations
- Actual HDMI/AV input switching is controlled by the TV firmware. If the TV does not expose an input-settings activity, use the TV remote's Source/Input button.
- Android may restrict access to some system settings screens.
- This is an initial starter build, not a finished Google TV clone. Test navigation and compatibility on the target TV before relying on it as the default launcher.
