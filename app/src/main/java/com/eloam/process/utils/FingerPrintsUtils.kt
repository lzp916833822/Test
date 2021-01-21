package com.eloam.process.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.eloam.process.MyApp
import com.eloam.process.callBack.FingerprintsCallBack
import com.eloam.process.callBack.OpenFingerprintsCallBack
import com.eloam.process.ui.WelcomeActivity
import com.za.finger.ZAandroid
import com.zaz.sdk.ukey.Tool
import kotlinx.coroutines.delay

class FingerPrintsUtils {
    companion object {
        private const val TAG = "FingerPrintsUtils"
        private const val ACTION_USB_PERMISSION = "com.eloam.process.Finger.USB_PERMISSION"
        private const val PS_NO_FINGER = 0x02
        private const val PS_OK = 0x00
        private const val CHAR_BUFFER_A = 0x01
        private const val DEV_ADD_R = -0x1

    }

    private var mPermissionIntent: PendingIntent? = null
    private var zazapi: ZAandroid? = null
    private var mhKey = 0

    private var mUsbManager: UsbManager? = null
    private var mUsbDevice: UsbDevice? = null
    private var mOpenFingerprintsCallBack: OpenFingerprintsCallBack? = null

    private suspend fun inti() {
        mUsbManager =
            MyApp.getApplication().getSystemService(Context.USB_SERVICE) as UsbManager? // 启动服务进程

        requestPermission()

    }

    suspend fun readChar(fingerprintsCallBack: FingerprintsCallBack?) {

        try {
            while (zazapi?.ZAZGetImage(DEV_ADD_R) != PS_NO_FINGER) {
                delay(100)
            }

            while (zazapi?.ZAZGetImage(DEV_ADD_R) == PS_NO_FINGER) {
                delay(100)
            }

            if (zazapi != null && zazapi?.ZAZGenChar(DEV_ADD_R, CHAR_BUFFER_A) == PS_OK) {
                LogUtils.d(TAG, "readChar Success", WelcomeActivity.UPLOADING_TIME,1)
                fingerprintsCallBack?.onSuccess("PS_OK")
            } else {
                fingerprintsCallBack?.onFailure()
                LogUtils.d(TAG, "readChar failure", WelcomeActivity.UPLOADING_TIME,1)

            }
        } catch (e: Exception) {
            fingerprintsCallBack?.onFailure()
            LogUtils.e(TAG, "readChar ${e.message}", WelcomeActivity.UPLOADING_TIME,1)
        }


    }

    suspend fun openUsbDevices(openFingerprintsCallBack: OpenFingerprintsCallBack) {
        mOpenFingerprintsCallBack = openFingerprintsCallBack
        inti()


    }

    private fun openFingerPrints() {
        if (mUsbManager!!.hasPermission(mUsbDevice)) {
            try {
                zazapi = ZAandroid(mUsbManager, mUsbDevice)
                val key = IntArray(1)
                val ret = zazapi!!.ZAZOpenDevice(0, 0, 0, 0, 0, 0)
                if (ret == ZAandroid.DEVICE_SUCCESS) {
                    mhKey = key[0]
                    mOpenFingerprintsCallBack?.success()
                    LogUtils.i(TAG, "open device success key :" + Tool.int2HexStr(mhKey),
                        WelcomeActivity.UPLOADING_TIME,1)
                } else {
                    mOpenFingerprintsCallBack?.failure()
                    LogUtils.i(TAG, "open device fail errCode :$ret",
                        WelcomeActivity.UPLOADING_TIME,1) //Tool.int2HexStr(ret));
                }
            } catch (e: Exception) {
                mOpenFingerprintsCallBack?.failure()
                LogUtils.e(TAG, "Exception: => ${e.message}", WelcomeActivity.UPLOADING_TIME,1)
            }
        } else {
            LogUtils.e(TAG, "openFingerPrints: => failure", WelcomeActivity.UPLOADING_TIME,1)
            mOpenFingerprintsCallBack?.failure()

        }


    }

    private suspend fun requestPermission() {
        for (device in mUsbManager!!.deviceList.values) {
            if (device.vendorId == 8457 && device.productId == 30264) {
                mUsbDevice = device
                LogUtils.i(TAG, "requestPermission: =>${mUsbManager!!.hasPermission(mUsbDevice)}",
                    WelcomeActivity.UPLOADING_TIME,1)
                if (!mUsbManager!!.hasPermission(mUsbDevice)) {
                    mPermissionIntent =
                        PendingIntent.getBroadcast(MyApp.getApplication(), 0, Intent(ACTION_USB_PERMISSION), 0)
                    val filter = IntentFilter()
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
                    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
                    MyApp.getApplication().registerReceiver(mUsbReceiver, filter)
                    mUsbManager!!.requestPermission(mUsbDevice, mPermissionIntent)
                    delay(5000)
                    openFingerPrints()

                } else {
                    openFingerPrints()

                }


            }
        }
    }

    fun onDestroy() {
        zazapi?.ZAZCloseDeviceEx()
        zazapi = null
        if (mPermissionIntent != null)
            MyApp.getApplication().unregisterReceiver(mUsbReceiver)
    }


    // 捕获usb的插拔消息
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // 收到消息
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            LogUtils.i(TAG, "UsbManager==$action", WelcomeActivity.UPLOADING_TIME,1)
            when {
                UsbManager.ACTION_USB_DEVICE_ATTACHED == action -> {

                }
                UsbManager.ACTION_USB_DEVICE_DETACHED == action -> {
//                    mOpenFingerprintsCallBack?.failure()
                    zazapi?.ZAZCloseDeviceEx()
                    zazapi = null
                    LogUtils.e(TAG, "BroadcastReceiver: => failure",
                        WelcomeActivity.UPLOADING_TIME,1)

                }

            }

        }
    }


}