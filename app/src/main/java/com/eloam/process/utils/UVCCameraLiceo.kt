package com.eloam.process.utils

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.usb.UsbDevice
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.eloam.process.R
import com.serenegiant.usb.DeviceFilter
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.UVCCamera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object UVCCameraLiceo {
    val mainRoot = Environment.getExternalStorageDirectory().toString() + "/GWDemo/"

    private val mSync = Any()
    private val mSyncOne = Any()

    private var mCameraRGB: UVCCamera? = null
    private var mCameraIR: UVCCamera? = null

    private var mPreviewSurfaceOne: Surface? = null

    interface OnMyDevConnectListener {
        fun onConnectDev(device: UsbDevice, ctrlBlock: USBMonitor.UsbControlBlock)
    }

    fun registerUsbMonitor(mUSBMonitor: USBMonitor) {
        mUSBMonitor?.register()
        synchronized(mSyncOne) {
            if (mCameraRGB != null) {
                mCameraRGB!!.startPreview()
            }
        }
    }

    fun unRegisterUsbMonitor(mUSBMonitor: USBMonitor){
        synchronized(mSync) {
            mUSBMonitor?.unregister()
        }
    }

    fun initUSBMonitor(context: Context, deviceConnectListener: UVCCameraLiceo.OnMyDevConnectListener) : USBMonitor{
        return USBMonitor(context.applicationContext, object : USBMonitor.OnDeviceConnectListener {
            override fun onConnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?, createNew: Boolean) {
                deviceConnectListener.onConnectDev(device!!, ctrlBlock!!)
            }

            override fun onCancel(device: UsbDevice?) {}

            override fun onAttach(device: UsbDevice?) {}

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {}

            override fun onDettach(device: UsbDevice?) {}
        })
    }

    fun requestPermissionRGB(context: Context, mUSBMonitor: USBMonitor){
        val devList = getUsbDeviceList(context, mUSBMonitor)
        var device: UsbDevice? = null
        if (devList == null || devList.size < 0) {
            return
        }
        for (usbDevice in devList) {
            val productid = String.format("%x", usbDevice!!.productId)
            Log.e("zzkong", " pida: $productid")
            if (productid == "6684") {
                device = usbDevice
            }
        }
        Thread(Runnable { // wait for camera created
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Log.e("zzkong", "打开RGB：" + String.format("%x", device!!.productId))
            mUSBMonitor.requestPermission(device)
        }).start()
    }

    fun openRGBCamera(textureView: TextureView, ctrlBlock: USBMonitor.UsbControlBlock, mIFrameCallbackOne: IFrameCallback) {
        synchronized(mSyncOne) {
            try {
                val camera = UVCCamera()
                camera.open(ctrlBlock)
                camera.setFrameCallback(mIFrameCallbackOne, 4)
                if (mPreviewSurfaceOne != null) {
                    mPreviewSurfaceOne!!.release()
                    mPreviewSurfaceOne = null
                }
                // Set preview size
                try {
                    camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG)
                } catch (e: IllegalArgumentException) {
                    // fallback to YUV mode
                    try {
                        camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE)
                    } catch (e1: IllegalArgumentException) {
                        camera.destroy()
                        return
                    }
                }
                // Set preview surface
                val st = textureView.surfaceTexture
                if (st != null) {
                    mPreviewSurfaceOne = Surface(st)
                    camera.setPreviewDisplay(mPreviewSurfaceOne)
                    camera.startPreview()
                    synchronized(mSyncOne) {
                        mCameraRGB = camera
                    }
                } else {
                      Log.e("zzkong", "open 红外 camera failed ing")
                }

            } catch (e: UnsupportedOperationException) {
                  Log.e("zzkong", "open 红外 camera failed: \n" + e.message)
            }
        }
    }

    fun releaseRGBCamera() {
            MainScope().launch(Dispatchers.Main) {
                synchronized(mSyncOne) {
                    if (mCameraRGB != null) {
                        try {
                            mCameraRGB!!.setStatusCallback(null)
                            mCameraRGB!!.setButtonCallback(null)
                            mCameraRGB!!.close()
                            if (null != mCameraRGB) {
                                mCameraRGB!!.destroy()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        mCameraRGB = null
                    }
                    if (mPreviewSurfaceOne != null) {
                        mPreviewSurfaceOne!!.release()
                        mPreviewSurfaceOne = null
                    }
                }
            }

    }

    fun destroyUSBMonitor(mUSBMonitor: USBMonitor){
        mUSBMonitor.destroy()
    }

    fun getUsbDeviceList(context: Context, mUSBMonitor: USBMonitor): List<UsbDevice?> {
        val deviceFilters = DeviceFilter.getDeviceFilters(context, R.xml.device_filter)
        return mUSBMonitor.getDeviceList(deviceFilters[0])
    }

    fun getDeviceList(context: Context, mUSBMonitor: USBMonitor): MutableIterator<UsbDevice>? {
        return mUSBMonitor.getDevices()
    }

    fun saveYuv2PNGOrJPG(path: String, data: ByteArray, type: Int) : String {
        val yuvImage = YuvImage(data, ImageFormat.NV21, 640, 480, null)
        var file: File? = null
        val bos = ByteArrayOutputStream(data.size)
        val result = yuvImage.compressToJpeg(Rect(0, 0, 640, 480), 100, bos)
        if (result) {
            val buffer = bos.toByteArray()
            val files = File(path)
            files.mkdir()
            if (type == 1) {
                file = File(path + getTimeBySystem("yyyy.MM.dd-HH:mm:ss") + ".jpg")
            } else {
                file = File(path + getTimeBySystem("yyyy.MM.dd-HH:mm:ss") + ".png")
            }
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                // fixing bm is null bug instead of using BitmapFactory.decodeByteArray
                fos.write(buffer)
                fos.close()
                return file.path
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }


    private fun getTimeBySystem(dateFormat: String): String? {
        val simpleDateFormat = SimpleDateFormat(dateFormat) // yyyy.MM.dd-HH:mm:ss
        //获取当前时间
        val date = Date(System.currentTimeMillis())
        return simpleDateFormat.format(date)
    }
}