package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import com.topjohnwu.superuser.Shell
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    private lateinit var usbHelper: UsbHelper

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbHelper = UsbHelper(this)
        // Kiểm tra thiết bị USB
        usbHelper.listUsbDevices()
        val root = isDeviceRooted()
        Log.d("Check root", "root Output: $root")


        val result = runRootCommandManually("ls -l /mnt/media_rw/79F0-629D")
        Log.d("command root","Command Result: $result")
        val url_local = "/mnt/media_rw/79F0-629D"
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurface
                ) {

                    VideoPlayerUSBScreen(url_local)
                }
            }
        }
    }

    fun isDeviceRooted(): Boolean {
        val shell = Shell.getShell()
        return shell.isRoot
    }

    fun runRootCommandManually(command: String): String? {
        return try {
            val process = ProcessBuilder("su", "-c", command)
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun readVideoPathAsRoot(videoPath: String): String? {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = process.outputStream
            outputStream.write("ls $videoPath\n".toByteArray())
            outputStream.flush()
            outputStream.close()

            process.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
