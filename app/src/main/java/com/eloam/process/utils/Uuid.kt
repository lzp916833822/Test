package com.eloam.process.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.net.NetworkInterface
import java.util.*


object Uuid {
    // Android Id
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ANDROID_ID
        )
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * @param context
     * @return
     */
    private fun getMacDefault(context: Context?): String? {
        var mac: String? = null
        if (context == null) {
            return mac
        }
        val wifi = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
            ?: return mac
        var info: WifiInfo? = null
        try {
            info = wifi.connectionInfo
        } catch (e: Exception) {
        }
        if (info == null) {
            return null
        }
        mac = info.macAddress
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH)
        }
        return mac
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    private fun getMacAddress(): String? {
        var WifiAddress: String? = null
        try {
            WifiAddress =
                BufferedReader(FileReader(File("/sys/class/net/wlan0/address"))).readLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return WifiAddress
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     * @return
     */
    private fun getMacFromHardware(): String? {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            Log.d("Utils", "all:" + all.size)
            for (nif in all) {
                if (nif.name != "wlan0") continue
                val macBytes: ByteArray = nif.hardwareAddress ?: return null
                Log.d("Utils", "macBytes:" + macBytes.size + "," + nif.name)
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * * 获取手机型号
     * *
     * * @return  手机型号
     */
    val systemModel: String
        get() = Build.MODEL

    /**
     * 得到全局唯一UUID
     */
    fun getUUID(context: Context): String? {
        val uuid = getMacAddress()
        return if (TextUtils.isEmpty(uuid)){
            ""
        }else{
            uuid?.toUpperCase(Locale.ROOT)
        }
    }
}