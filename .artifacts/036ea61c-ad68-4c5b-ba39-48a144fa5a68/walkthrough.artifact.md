# Walkthrough - Add Help Button to Header

I have added a help button to the top app bar of the main screen. This button allows users to quickly access the project's GitHub repository.

## Changes

### String Resources
I added the `help_description` string to all supported languages for accessibility:
- [strings.xml (Default)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values/strings.xml)
- [strings.xml (English)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-en/strings.xml)
- [strings.xml (Russian)](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-ru/strings.xml)

### UI Updates
In [MainActivity.kt](file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/MainActivity.kt), I updated the `TopAppBar` to include an action button:
- Used `Icons.AutoMirrored.Filled.HelpOutline` for the "?" icon.
- Implemented an `IconButton` that launches an `ACTION_VIEW` intent to `https://github.com/draumaz/AlbumMusicWallpaper`.

## Verification Results

### Automated Tests
- Ran `gradle build` which finished successfully.

### Manual Verification
- You can now see the help button in the top right corner of the app's header. Clicking it should open the browser to the GitHub repository.

render_diffs(file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/java/com/lucasvinicius/musicwallpaper/MainActivity.kt)
render_diffs(file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values/strings.xml)
render_diffs(file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-en/strings.xml)
render_diffs(file:///home/emma/android-repos/AlbumMusicWallpaper/app/src/main/res/values-ru/strings.xml)
