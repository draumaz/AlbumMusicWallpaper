# Add Help Button to Header

The goal is to add a help button (marked with "?") in the top app bar of the main screen. This button will redirect the user to the project's GitHub repository.

## Proposed Changes

### Resource Files

#### [MODIFY] [strings.xml](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values/strings.xml)
- Add `help_description` string for accessibility.

#### [MODIFY] [strings.xml (en)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-en/strings.xml)
- Add `help_description` string in English.

#### [MODIFY] [strings.xml (ru)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-ru/strings.xml)
- Add `help_description` string in Russian.

### UI Components

#### [MODIFY] [MainActivity.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/MainActivity.kt)
- Import `android.net.Uri`.
- Import `androidx.compose.material.icons.automirrored.filled.HelpOutline`.
- Update `TopAppBar` to include an `actions` block with an `IconButton`.
- The `IconButton` will use `Icons.AutoMirrored.Filled.HelpOutline` and launch an intent to `https://github.com/draumaz/AlbumMusicWallpaper`.

## Verification Plan

### Automated Tests
- I will run a build to ensure there are no compilation errors.

### Manual Verification
- Deploy the app to a device or emulator.
- Verify that the "?" button appears in the top right corner of the header.
- Click the button and ensure it opens the correct GitHub URL in a browser.
