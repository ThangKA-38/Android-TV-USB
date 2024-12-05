package com.example.myapplication
import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.storage.StorageManager
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import java.io.BufferedReader
import java.io.InputStreamReader

class UsbHelper(private val context: Context) {

    @OptIn(UnstableApi::class)
    fun listUsbDevices() {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            Log.d("UsbHelper", "USB device found: ${device.deviceName}")
        }
    }
}