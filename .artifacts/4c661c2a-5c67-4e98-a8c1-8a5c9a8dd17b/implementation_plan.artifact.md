# Add Blur Slider to Appearance Settings

This plan adds a blur slider to the Appearance section in the app settings. This slider will allow users to softly blur the album art on their wallpaper.

## User Review Required

> [!IMPORTANT]
> The blur effect for the static album art will be implemented using `RenderEffect` on Android 12+ (API 31+) for optimal performance. For older versions (API 28-30), a fast scaling trick (downscale and upscale) will be used to achieve a soft blur effect.

## Proposed Changes

### Data Layer

#### [MODIFY] [WallpaperStateStore.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/data/local/WallpaperStateStore.kt)
- Add `BLUR_LEVEL` key to DataStore.
- Add `blurLevelFlow` (defaulting to 0%).
- Add `saveBlurLevel(level: Int)` function.

### UI Layer

#### [MODIFY] [MainActivity.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/MainActivity.kt)
- Collect `blurLevel` from `WallpaperStateStore`.
- Add `BlurCard` composable (similar to `DimmingCard`).
- Add `BlurCard` to the Appearance section in the `Scaffold` content.

#### [MODIFY] [strings.xml (en)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-en/strings.xml)
- Add `blur_level_label`.

#### [MODIFY] [strings.xml (ru)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-ru/strings.xml)
- Add `blur_level_label` (translated).

#### [MODIFY] [strings.xml (default)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values/strings.xml)
- Add `blur_level_label` (translated).

### Wallpaper Rendering Layer

#### [MODIFY] [WallpaperImageRenderer.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/wallpaper/WallpaperImageRenderer.kt)
- Update `draw`, `drawCrossfade`, and `drawFadeOut` methods to accept `blurLevel: Int`.
- Update `drawBitmapToCanvas` to apply blur using `RenderEffect` (API 31+) or scaling trick (API < 31).

#### [MODIFY] [AnimatedWallpaperService.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/wallpaper/AnimatedWallpaperService.kt)
- Add `currentBlurLevel` state.
- Observe `blurLevelFlow` from `WallpaperStateStore`.
- Pass `currentBlurLevel` to all `imageRenderer` calls.

## Verification Plan

### Manual Verification
- Deploy the app to an Android device/emulator.
- Navigate to the Appearance section in the app.
- Adjust the Blur slider and verify that the wallpaper (if static album art is displayed) blurs accordingly.
- Verify that the Dim slider still works as expected.
- Verify that changing both sliders works together correctly.
