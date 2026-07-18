# Blur Slider Implementation Walkthrough

I have added a new "Blur" slider to the Appearance section of the Music Wallpaper app. This feature allows users to softly blur the static album art displayed on their wallpaper.

## Changes Made

### 1. Data Persistence
- **[WallpaperStateStore.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/data/local/WallpaperStateStore.kt)**: Added `BLUR_LEVEL` key and methods to save and retrieve the blur percentage (0-100%).

### 2. User Interface
- **[MainActivity.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/MainActivity.kt)**:
    - Added a `BlurCard` composable containing a slider for user input.
    - Integrated the blur slider into the main UI within the Appearance section.
- **Localization**: Added translations for the blur label in:
    - [strings.xml (English)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-en/strings.xml)
    - [strings.xml (Russian)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-ru/strings.xml)
    - [strings.xml (Portuguese)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values/strings.xml)

### 3. Wallpaper Rendering Logic
- **[WallpaperImageRenderer.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/wallpaper/WallpaperImageRenderer.kt)**:
    - Implemented high-performance blur using `RenderEffect` for Android 12+ (API 31+) via reflection to ensure compatibility with the project's current configuration.
    - Provided a fallback `BlurMaskFilter` for devices running API 28-30.
    - Updated all drawing methods (static, crossfade, fadeout) to respect the blur level.
- **[AnimatedWallpaperService.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/wallpaper/AnimatedWallpaperService.kt)**:
    - Added an observer for the blur level from `WallpaperStateStore`.
    - Triggered a wallpaper redraw whenever the blur level changes.

## Verification Results

### Automated Build
- Ran `:app:assembleDebug` successfully.

### Manual Verification Suggestion
1. Open the app and navigate to the **Appearance** section.
2. Use the **Static Cover Blur** slider to adjust the blur level.
3. If static album art is currently active on your wallpaper, you should see the blur effect apply in real-time.
