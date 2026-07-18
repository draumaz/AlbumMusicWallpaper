package com.lucasvinicius.musicwallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.lucasvinicius.musicwallpaper.data.model.WallpaperContent
import com.lucasvinicius.musicwallpaper.notification.MusicNotificationListenerService
import com.lucasvinicius.musicwallpaper.ui.theme.MusicWallpaperTheme
import com.lucasvinicius.musicwallpaper.wallpaper.AnimatedWallpaperService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val savedPath = withContext(Dispatchers.IO) {
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        val file = File(filesDir, "default_wallpaper.jpg")
                        val outputStream = FileOutputStream(file)
                        inputStream?.copyTo(outputStream)
                        inputStream?.close()
                        outputStream.close()
                        file.absolutePath
                    } catch (e: Exception) {
                        null
                    }
                }

                if (savedPath != null) {
                    val app = application as App
                    app.wallpaperStateStore.saveDefaultWallpaper(savedPath)
                    Toast.makeText(this@MainActivity, getString(R.string.toast_wallpaper_saved), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as App

        setContent {
            MusicWallpaperTheme {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                
                var isNotificationEnabled by remember { mutableStateOf(checkNotificationAccess()) }
                val wallpaperContent by app.wallpaperStateStore.contentFlow.collectAsStateWithLifecycle(initialValue = null)
                val dimLevel by app.wallpaperStateStore.dimLevelFlow.collectAsStateWithLifecycle(initialValue = 30)
                val blurLevel by app.wallpaperStateStore.blurLevelFlow.collectAsStateWithLifecycle(initialValue = 0)

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            isNotificationEnabled = checkNotificationAccess()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Notification Status Card
                        StatusCard(
                            enabled = isNotificationEnabled,
                            onActionClick = {
                                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            }
                        )

                        // Actions Section
                        SectionHeader(stringResource(R.string.actions_section_title))
                        
                        ActionCard(
                            text = stringResource(R.string.btn_choose_wallpaper),
                            icon = Icons.Default.Wallpaper,
                            onClick = {
                                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                                    putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(this@MainActivity, AnimatedWallpaperService::class.java))
                                }
                                startActivity(intent)
                            }
                        )

                        ActionCard(
                            text = stringResource(R.string.btn_choose_default_photo),
                            icon = Icons.Default.Image,
                            onClick = { pickImageLauncher.launch("image/*") }
                        )

                        // Appearance Section
                        SectionHeader(stringResource(R.string.dimming_section_title))
                        
                        DimmingCard(
                            dimLevel = dimLevel,
                            onDimChange = { newValue ->
                                lifecycleScope.launch {
                                    app.wallpaperStateStore.saveDimLevel(newValue.toInt())
                                }
                            }
                        )

                        BlurCard(
                            blurLevel = blurLevel,
                            onBlurChange = { newValue ->
                                lifecycleScope.launch {
                                    app.wallpaperStateStore.saveBlurLevel(newValue.toInt())
                                }
                            }
                        )

                        // Live Status Section
                        SectionHeader(stringResource(R.string.live_status_header))
                        
                        LiveStatusCard(wallpaperContent)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    private fun checkNotificationAccess(): Boolean {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners").orEmpty()
        return enabledListeners.contains(ComponentName(this, MusicNotificationListenerService::class.java).flattenToString())
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun StatusCard(enabled: Boolean, onActionClick: () -> Unit) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(if (enabled) R.string.notification_access_allowed else R.string.notification_access_blocked),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!enabled) {
                Button(onClick = onActionClick) {
                    Text(stringResource(R.string.btn_notification_access).substringAfter(". "))
                }
            }
        }
    }
}

@Composable
fun ActionCard(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text.substringAfter(". "), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DimmingCard(dimLevel: Int, onDimChange: (Float) -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.dim_level_label, dimLevel),
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = dimLevel.toFloat(),
                onValueChange = onDimChange,
                valueRange = 0f..100f,
                steps = 100
            )
        }
    }
}

@Composable
fun BlurCard(blurLevel: Int, onBlurChange: (Float) -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.blur_level_label, blurLevel),
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = blurLevel.toFloat(),
                onValueChange = onBlurChange,
                valueRange = 0f..100f,
                steps = 100
            )
        }
    }
}

@Composable
fun LiveStatusCard(content: WallpaperContent?) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (content == null) {
                Text("-", style = MaterialTheme.typography.bodyMedium)
            } else {
                StatusItem(stringResource(R.string.track_label, content.trackTitle ?: "-"))
                StatusItem(stringResource(R.string.artist_label, content.trackArtist ?: "-"))
                StatusItem(stringResource(R.string.album_label, content.trackAlbum ?: "-"))
                StatusItem(stringResource(R.string.type_label, content.contentType.name))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                StatusItem(stringResource(R.string.animated_url_label, content.animatedUrl ?: "-"))
                StatusItem(stringResource(R.string.static_path_label, content.staticImagePath ?: "-"))
            }
        }
    }
}

@Composable
fun StatusItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MusicWallpaperTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusCard(enabled = true, onActionClick = {})
                SectionHeader("Ações")
                ActionCard(text = "Escolher papel de parede", icon = Icons.Default.Wallpaper, onClick = {})
                SectionHeader("Aparência")
                DimmingCard(dimLevel = 30, onDimChange = {})
                BlurCard(blurLevel = 10, onBlurChange = {})
                SectionHeader("Status")
                LiveStatusCard(null)
            }
        }
    }
}
