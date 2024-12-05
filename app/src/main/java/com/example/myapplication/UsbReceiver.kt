package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import java.io.File

class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_MEDIA_MOUNTED -> {
                val usbPath = intent.data?.path
                if (usbPath != null) {
                    Log.d("UsbReceiver", "USB mounted at: $usbPath")
                    // Gọi hàm handleUsbFiles để xử lý file trong thư mục video/
                    handleUsbFiles(usbPath)
                } else {
                    Log.d("UsbReceiver", "USB path is null")
                }
            }
            Intent.ACTION_MEDIA_REMOVED -> {
                Log.d("UsbReceiver", "USB removed")
            }
        }
    }

    private fun handleUsbFiles(usbPath: String) {
        val videoDir = File("$usbPath/video")
        if (videoDir.exists() && videoDir.isDirectory) {
            val videoFiles = videoDir.listFiles { file -> file.isFile }
            videoFiles?.forEach { file ->
                Log.d("UsbReceiver", "Found video file: ${file.name}")
                // Thêm mã để xử lý file video
            }
        } else {
            Log.d("UsbReceiver", "Video directory not found")
        }
    }
}

@Composable
fun VideoPlayerScreen(viewModel: VideoPlayerViewModel) {
    val usbPath = viewModel.usbPath

    if (usbPath.value != null) {
        VideoPlayerUSBScreen(videoUrl = usbPath.value.toString())
    }
}

class VideoPlayerViewModel : ViewModel() {
    private val _usbPath = MutableLiveData<String?>()
    val usbPath: LiveData<String?> = _usbPath

    fun setUsbPath(path: String?) {
        _usbPath.value = path
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerUSBScreen(videoUrl: String) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekForwardIncrementMs(10)
            .setSeekBackIncrementMs(10)
            .setMediaSourceFactory(
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            )
            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            .build()
            .apply {
                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(videoUrl))
                    .build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }

    DisposableEffect(
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}
