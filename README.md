# 🎵 Music Wallpaper (Live)

A native Android application that transforms your device's lock screen and home screen into an immersive musical experience. It detects the currently playing track from your favorite media player (Spotify, Apple Music, etc.) and automatically applies the animated album cover (Canvas) or high-resolution static artwork as the system wallpaper.

## ✨ Features

* **Real-Time Synchronization:** Uses `NotificationListenerService` to detect track changes instantly in the background.
* **Animated Videos (Canvas):** Smooth playback of HLS videos using `Media3/ExoPlayer` directly on the wallpaper surface.
* **Smart Fallback:** If the track doesn't have an animated video, the app fetches high-resolution static artwork (via iTunes API).
* **Dimming & Blur Control:** Real-time sliders allow users to adjust dimming (0-100%) and blur intensity (0-100%) for static artwork, ensuring optimal UI legibility.
* **Idle/Paused Mode:** Set a custom photo from your gallery to be displayed when no music is playing.
* **Modern UI:** Built with Jetpack Compose and Material 3 for a fluid and responsive configuration experience.

## 🛠️ Technical Challenges & Optimizations

Building a continuous Live Wallpaper requires dealing with hardware fragmentation and Android OS quirks. This project features architectural solutions for complex problems:

1. **Advanced Memory Management (OOM Prevention):**
   * **Bitmap Recycling:** Explicitly calls `bitmap.recycle()` in the wallpaper engine to free native memory immediately after transitions, preventing leaks during long sessions.
   * **Capped Metadata Cache:** Uses an `LruCache` to limit memory growth from artwork lookup results.
   * **Buffer Reuse:** Reuses internal bitmap buffers for blur effects to minimize GC pressure and "jank."

2. **Battery & Efficiency:**
   * **Visibility-Aware Rendering:** Animations (crossfades/fades) and video playback are strictly tied to the `isVisible` state of the wallpaper engine. Drawing stops the moment the user turns off the screen or enters another app.
   * **Coroutine Lifecycle:** Background tasks are tied to `Service` and `Engine` lifecycles, ensuring no orphan jobs consume resources when the app is inactive.

3. **Codec Handling and HDR (False Negatives):**
   Many music APIs return videos encoded in heavy profiles, such as 10-bit HEVC (HDR). The app features an error *Listener* coupled to ExoPlayer that intercepts hardware initialization failures and triggers a silent fallback to static artwork.

4. **Surface Lock Bypass:**
   Implemented a pixel format reconfiguration technique (`PixelFormat.RGBX_8888` vs `RGBA_8888`) to force the OS to cleanly recreate surfaces when switching between Canvas (Photo) and ExoPlayer (Video).

## 💻 Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose & Material 3
* **Architecture:** Coroutines & Kotlin Flow (Reactivity)
* **Local Storage:** Jetpack DataStore (Preferences)
* **Networking:** Retrofit2 & OkHttp3
* **Media:** AndroidX Media3 (ExoPlayer & HLS)
* **Core Components:** `WallpaperService`, `NotificationListenerService`

## 🚀 Getting Started

1. **Permissions:** Ensure "Notification Access" is granted so the app can detect music metadata.
2. **Setup:** Open the app, set a default photo, and click "Set Wallpaper" to choose "Music Wallpaper" as your live wallpaper.
3. **Customize:** Use the sliders to adjust the appearance to your liking.
