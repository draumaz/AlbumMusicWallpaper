# Implementation Plan - Settings Redesign & Localization

Redesign the settings page (`MainActivity`) to follow Material 3 Expressive-aesthetic principles using Jetpack Compose and add support for English and Russian languages.

## User Review Required

> [!IMPORTANT]
> The current application UI is implemented programmatically in `MainActivity.kt` using legacy Android Views (LinearLayout, Button, etc.). I will be migrating this to Jetpack Compose to achieve the requested "Material 3 Expressive-aesthetic".

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///home/emma/android-repos/AlbumMusicWallpaper/gradle/libs.versions.toml)
- Add Jetpack Compose BOM and related libraries (UI, Material3, Activity-Compose, Lifecycle-Runtime-Compose).

#### [MODIFY] [app/build.gradle.kts](file:///home/emma/android-repos/AlbumMusicWallpaper/app/build.gradle.kts)
- Enable Compose build feature.
- Add Compose dependencies.

### Resources & Localization

#### [MODIFY] [values/strings.xml](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values/strings.xml)
- Extract all hardcoded strings from `MainActivity.kt` into the default `strings.xml` (maintaining Portuguese).

#### [NEW] [values-en/strings.xml](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-en/strings.xml)
- Add English translations for all strings.

#### [NEW] [values-ru/strings.xml](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-ru/strings.xml)
- Add Russian translations for all strings.

### UI Redesign

#### [NEW] [Theme.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/ui/theme/Theme.kt)
- Define the Material 3 theme, including support for Dynamic Color (Android 12+).

#### [NEW] [Color.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/ui/theme/Color.kt)
- Define Material 3 color tokens.

#### [NEW] [Type.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/ui/theme/Type.kt)
- Define expressive typography.

#### [MODIFY] [MainActivity.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/MainActivity.kt)
- Replace legacy View-based UI with `setContent { ... }` using Jetpack Compose.
- Use `LargeTopAppBar` and `ElevatedCard` for an expressive feel.
- Implement the settings logic (notification access, wallpaper picker, image selection, dim level) using Compose state.

## Verification Plan

### Automated Tests
- Run `gradlew assembleDebug` to ensure the project builds correctly with Compose.

### Manual Verification
- Deploy to an Android device/emulator.
- Verify the new Material 3 UI.
- Verify functionality:
    - Notification access button opens settings.
    - Wallpaper button opens live wallpaper picker.
    - Photo picker button opens gallery and saves the selection.
    - Dim slider updates the value and saves it.
- Verify localization by changing the device language to English and Russian.
