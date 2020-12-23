package com.eloam.process.utils

import android.content.Context
import android.hardware.usb.UsbDevice
import android.view.Surface
import android.view.TextureView
import com.eloam.process.R
import com.eloam.process.callBack.OpenStateCallBack
import com.serenegiant.usb.DeviceFilter
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.UVCCamera

/**
 * @author: lico
 * @create：2020/7/8
 * @describe：
 */
object UVCCameraUtil {
    private const val TAG = "UVCCameraUtil"

    // 双目彩色摄像头
    private val mRGBSync = Any()
    private var mRGBCamera: UVCCamera? = null
    private var mRGBPreview: Surface? = null

    // 双目红外摄像头
    private val mIRSync = Any()
    private var mIRCamera: UVCCamera? = null
    private var mIRPreview: Surface? = null

    // 高拍仪摄像头
    private val mHighSync = Any()
    private var mHighCamera: UVCCamera? = null
    private var mHighPreview: Surface? = null

    // 高拍仪副摄像头
    private val mDeputySync = Any()
    private var mDeputyCamera: UVCCamera? = null
    private var mDeputyPreview: Surface? = null

    interface OnMyDevConnectListener {
        fun onConnectDev(device: UsbDevice, ctrlBlock: USBMonitor.UsbControlBlock)
    }

    fun initUSBMonitor(
        context: Context,
        onMyDevConnectListener: OnMyDevConnectListener
    ): USBMonitor {
        return USBMonitor(context, object : USBMonitor.OnDeviceConnectListener {
            override fun onConnect(
                device: UsbDevice,
                ctrlBlock: USBMonitor.UsbControlBlock,
                createNew: Boolean
            ) {
                onMyDevConnectListener.onConnectDev(device, ctrlBlock)

            }

            override fun onCancel(device: UsbDevice?) {

            }

            override fun onAttach(device: UsbDevice?) {

            }

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {

            }

            override fun onDettach(device: UsbDevice?) {

            }

        })
    }

    fun requestPermission(
        context: Context,
        pid: String,
        delay: Long,
        userMonitor: USBMonitor
    ): Boolean {
        var isPermission = false
        val devList = getUsbDeviceList(context, userMonitor)
        var device: UsbDevice? = null
        if (devList.size < 0) {
            return true
        }
        for (usbDevice in devList) {
            val productId = String.format("%x", usbDevice!!.productId)
            LogUtils.e(TAG, "findById: $productId")
            if (productId == pid) {
                device = usbDevice
                isPermission = true
            }
        }
        Thread(Runnable { // wait for camera created
            try {
                Thread.sleep(delay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
//            LogUtils.e(TAG, "openPID：" + String.format("%x", device!!.productId))
            userMonitor.requestPermission(device)
        }).start()
        return isPermission
    }


    /**
     * 打开双目彩色摄像头
     */
    fun openRGBCamera(
        textureView: TextureView,
        ctrlBlock: USBMonitor.UsbControlBlock,
        mIFrameCallbackOne: IFrameCallback,
        openStateCallBack: OpenStateCallBack
    ) {
        synchronized(mRGBSync) {
            mRGBCamera = UVCCamera()
            mRGBCamera?.open(ctrlBlock)
            mRGBCamera?.setFrameCallback(mIFrameCallbackOne, 4)

            mRGBPreview?.release()
            mRGBPreview = null

            try {
                mRGBCamera?.setPreviewSize(
                    UVCCamera.DEFAULT_PREVIEW_WIDTH,
                    UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                    UVCCamera.FRAME_FORMAT_MJPEG
                )
            } catch (e: IllegalArgumentException) {
                try {
                    // fallback to YUV mode
                    mRGBCamera?.setPreviewSize(
                        UVCCamera.DEFAULT_PREVIEW_WIDTH,
                        UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                        UVCCamera.DEFAULT_PREVIEW_MODE
                    )
                } catch (e1: IllegalArgumentException) {
                    openStateCallBack.failure()
                    mRGBCamera?.destroy()
                    return
                }
            }

            val st = textureView.surfaceTexture
            if (st != null) {
                mRGBPreview = Surface(st)
                mRGBCamera?.setPreviewDisplay(mRGBPreview)
                mRGBCamera?.startPreview()
                openStateCallBack.success()

                LogUtils.e(TAG, "打开彩色摄像头成功: ")
            } else {
                openStateCallBack.failure()

                LogUtils.e(TAG, "open 彩色 camera failed ing")
            }

        }
    }

    /**
     * 打开双目红外摄像头
     */
    fun openIRCamera(
        textureView: TextureView,
        ctrlBlock: USBMonitor.UsbControlBlock,
        mIFrameCallbackOne: IFrameCallback,
        openStateCallBack: OpenStateCallBack
    ) {
        synchronized(mIRSync) {
            mIRCamera = UVCCamera()
            mIRCamera?.open(ctrlBlock)
            mIRCamera?.setFrameCallback(mIFrameCallbackOne, 4)

            mIRPreview?.release()
            mIRPreview = null

            try {
                mIRCamera?.setPreviewSize(
                    UVCCamera.DEFAULT_PREVIEW_WIDTH,
                    UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                    UVCCamera.FRAME_FORMAT_MJPEG
                )
            } catch (e: IllegalArgumentException) {
                try {
                    // fallback to YUV mode
                    mIRCamera?.setPreviewSize(
                        UVCCamera.DEFAULT_PREVIEW_WIDTH,
                        UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                        UVCCamera.DEFAULT_PREVIEW_MODE
                    )
                } catch (e1: IllegalArgumentException) {
                    openStateCallBack.failure()
                    mIRCamera?.destroy()
                    return
                }
            }

            val st = textureView.surfaceTexture
            if (st != null) {
                mIRPreview = Surface(st)
                mIRCamera?.setPreviewDisplay(mIRPreview)
                mIRCamera?.startPreview()
                openStateCallBack.success()

                LogUtils.e(TAG, "打开红外摄像头成功: ")
            } else {
                openStateCallBack.failure()
                LogUtils.e(TAG, "open 红外 camera failed, textureView 没有准备好")
            }
        }
    }

    /**
     * 打开高拍仪摄像头
     */
    fun openHighCamera(
        textureView: TextureView,
        ctrlBlock: USBMonitor.UsbControlBlock,
        mIFrameCallbackOne: IFrameCallback,
        openStateCallBack: OpenStateCallBack
    ) {
        synchronized(mHighSync) {
            mHighCamera = UVCCamera()
            mHighCamera?.open(ctrlBlock)
            mHighCamera?.setFrameCallback(mIFrameCallbackOne, 4)

            //  Log.e("zzkong", ": " + mHighCamera?.supportedSize);

            mHighPreview?.release()
            mHighPreview = null

            try {
                mHighCamera?.setPreviewSize(640, 480, UVCCamera.FRAME_FORMAT_YUYV)
            } catch (e: IllegalArgumentException) {
                LogUtils.e(TAG, "IllegalArgumentException:  ${e.message}")
                try {
                    // fallback to YUV mode
                    mHighCamera?.setPreviewSize(
                        UVCCamera.DEFAULT_PREVIEW_WIDTH,
                        UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                        UVCCamera.DEFAULT_PREVIEW_MODE
                    )
                } catch (e: IllegalArgumentException) {
                    LogUtils.e(TAG, "IllegalArgumentException: ${e.message}")
                    openStateCallBack.failure()
                    mHighCamera?.destroy()
                    return
                }
            }

            val st = textureView.surfaceTexture
            if (st != null) {
                mHighPreview = Surface(st)
                mHighCamera?.setPreviewDisplay(mHighPreview)
                mHighCamera?.startPreview()
                openStateCallBack.success()
                LogUtils.e(TAG, "打开高拍仪摄像头成功: ")
            } else {
                openStateCallBack.failure()

                LogUtils.e(TAG, "open 高拍仪 camera failed, textureView 没有准备好")
            }
        }
    }


    /**
     * 打开高拍仪副摄像头
     */
    fun openDeputyCamera(
        textureView: TextureView,
        ctrlBlock: USBMonitor.UsbControlBlock,
        mIFrameCallbackOne: IFrameCallback,
        openStateCallBack: OpenStateCallBack

    ) {
        synchronized(mDeputySync) {
            mDeputyCamera = UVCCamera()
            mDeputyCamera?.open(ctrlBlock)
            mDeputyCamera?.setFrameCallback(mIFrameCallbackOne, 1)

            //  Log.e("zzkong", ": " + mHighCamera?.supportedSize);

            mDeputyPreview?.release()
            mHighPreview = null

            try {
                mDeputyCamera?.setPreviewSize(640, 480, UVCCamera.FRAME_FORMAT_MJPEG)
            } catch (e: IllegalArgumentException) {
                LogUtils.e(TAG, "IllegalArgumentException: ${e.message}")
                try {
                    // fallback to YUV mode
                    mDeputyCamera?.setPreviewSize(
                        UVCCamera.DEFAULT_PREVIEW_WIDTH,
                        UVCCamera.DEFAULT_PREVIEW_HEIGHT,
                        UVCCamera.DEFAULT_PREVIEW_MODE
                    )
                } catch (e: IllegalArgumentException) {
                    LogUtils.e(TAG, "IllegalArgumentException: ${e.message}")
                    openStateCallBack.failure()
                    mDeputyCamera?.destroy()
                    return
                }
            }

            val st = textureView.surfaceTexture
            if (st != null) {
                mDeputyPreview = Surface(st)
                mDeputyCamera?.setPreviewDisplay(mDeputyPreview)
                mDeputyCamera?.startPreview()
                openStateCallBack.success()

                LogUtils.e(TAG, "打开副高拍仪摄像头成功: ")
            } else {
                openStateCallBack.failure()

                LogUtils.e(TAG, "open 高拍仪 camera failed, textureView 没有准备好")
            }
        }
    }

    /**
     * 关闭双目彩色摄像头
     */
    fun releaseRGBCamera() {
        mRGBCamera?.stopPreview()
        mRGBCamera?.close()
        mRGBCamera?.destroy()
        mRGBCamera = null
        mRGBPreview?.release()
        mRGBPreview = null
        LogUtils.e(TAG, "close releaseRGBCamera: ")

    }

    /**
     * 关闭双目红外摄像头
     */
    fun releaseIRCamera() {
        mIRCamera?.stopPreview()
        mIRCamera?.close()
        mIRCamera?.destroy()
        mIRCamera = null
        mIRPreview?.release()
        mIRPreview = null
        LogUtils.e(TAG, "close releaseIRCamera: ")

    }

    /**
     * 关闭高拍仪摄像头
     */
    fun releaseHighCamera() {
        mHighCamera?.stopPreview()
        mHighCamera?.close()
        mHighCamera?.destroy()
        mHighCamera = null
        mHighPreview?.release()
        mHighPreview = null
        LogUtils.e(TAG, "close releaseHighCamera: ")

    }

    /**
     * 关闭高拍仪副摄像头
     */
    fun releaseDeputyCamera() {
        mDeputyCamera?.stopPreview()
        mDeputyCamera?.close()
        mDeputyCamera?.destroy()
        mDeputyCamera = null
        mDeputyPreview?.release()
        mDeputyPreview = null
        LogUtils.e(TAG, "close releaseDeputyCamera: ")

    }

    private fun getUsbDeviceList(context: Context, mUSBMonitor: USBMonitor): List<UsbDevice?> {
        val deviceFilters = DeviceFilter.getDeviceFilters(context, R.xml.device_filter)
        return mUSBMonitor.getDeviceList(deviceFilters[0])
    }
}