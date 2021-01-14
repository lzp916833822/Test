package com.eloam.process.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.eloam.process.R
import com.eloam.process.callBack.IcCardCallBack
import com.eloam.process.callBack.IcCardReadCallBack
import com.eloam.process.data.DataRepository
import com.eloam.process.data.entity.*
import com.eloam.process.ui.WelcomeActivity
import com.eloam.process.usbutils.USBHelper
import com.eloam.process.utils.GoSonUtils
import com.eloam.process.utils.IcCardUtils
import com.eloam.process.utils.JUtils
import com.eloam.process.utils.LogUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.lico.core.base.BaseViewModel
import java.io.File
import java.util.*

/**
 * @author: lico
 * @create：2020/10/21
 * @describe：
 */
class MainViewModel(
    private val context: Context,
    private val app: Application,
    private val dataRepository: DataRepository
) : BaseViewModel(app) {

    companion object {
        const val ACTION_USB_PERMISSION = "com.eloam.process.USB_PERMISSION"
        const val TAG = "MainViewModel"
        const val DEVICE_INFO = "DeviceInfo"
    }

    var mIcCardUtils: IcCardUtils? = null

    //需要开启项
    var statueOpen: MutableLiveData<StatueOpen> = MutableLiveData()

    //打开状态
    var statueOpenDevice: MutableLiveData<StatueOpenDevice> = MutableLiveData()

    //打开状态需要显示界面
    var statueVisibility: MutableLiveData<Int> = MutableLiveData()

    //开启执行到某项动画
    var statueAnim: MutableLiveData<StatueAnim> = MutableLiveData()

    //执行的结果
    var statueResult: MutableLiveData<StatueResult> = MutableLiveData()

    //上传结果
    var uploadingFileResult: MutableLiveData<Int> = MutableLiveData()

    private val mDeviceInfo: MutableMap<String, String> = HashMap() // 用来存储设备信息和异常信息

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    private fun collectDeviceInfo(context: Context?) {
        try {
            val pm = context!!.packageManager // 获得包管理器
            val pi = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_ACTIVITIES
            ) // 得到该应用的信息，即主Activity
            if (pi != null) {
                val versionName =
                    if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                mDeviceInfo["versionName"] = versionName
                mDeviceInfo["versionCode"] = versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val fields =
            Build::class.java.declaredFields // 反射机制
        for (field in fields) {
            try {
                field.isAccessible = true
                mDeviceInfo[field.name] = field[""].toString()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取设备信息
     */
    fun getDeviceInfo(): String {
        collectDeviceInfo(context)
        val sb = StringBuilder()
        for ((key, value) in mDeviceInfo) {
            sb.append(key).append("=").append(value).append("\r\n")
        }
        return sb.toString()
    }

    @SuppressLint("NewApi")
    fun initIdCardUsbDev() {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val filter = IntentFilter()
        filter.addAction(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        context.registerReceiver(mUsbReceiver, filter)
        statueOpen.postValue(StatueOpen(4, requestPermission(usbManager)))

    }

    /**
     * 身份证模块请求usb权限
     */
    private fun requestPermission(usbManager: UsbManager): Int {
        var isIntiSuccess = -2
        for (device in usbManager.deviceList.values) {
            if ((device.vendorId == 1024 && device.productId == 50010)) {
                isIntiSuccess = -1
                if (!usbManager.hasPermission(device)) {
                    val intent = Intent(ACTION_USB_PERMISSION)
                    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
                    usbManager.requestPermission(device, pendingIntent)
                } else {
                    isIntiSuccess = 0
                }
            }
        }
        return isIntiSuccess
    }


    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        statueOpen.postValue(StatueOpen(4, 0))

                        LogUtils.e(
                            TAG, "USB is card get permission success",
                            WelcomeActivity.UPLOADING_TIME, 1
                        )
                        //USB授权成功
                    } else {
                        statueOpen.postValue(StatueOpen(4, -1))

                        LogUtils.e(
                            TAG, "USB is card get permission failure",
                            WelcomeActivity.UPLOADING_TIME, 1
                        )
                    }
                }
            }
        }
    }


    /**
     * 拍照结果显示
     */
    fun getTextSting(it: StatueResult): String {
        return when (it.state) {
            1 -> {
                context.getString(R.string.take_photo_success)
            }
            2 -> {
                context.getString(R.string.take_photo_failure)
            }
            else -> {
                ""
            }
        }
    }


    /**
     * 打开操作结果
     */
    fun getOpenDeviceTextSting(it: StatueOpenDevice): String {
        return when (it.state) {
            1 -> {
                context.getString(R.string.open_success)
            }
            2 -> {
                context.getString(R.string.open_failure)
            }
            else -> {
                ""
            }
        }
    }

    /**
     * 显示颜色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getOpenDeviceTextColor(it: StatueOpenDevice): Int {

        return when (it.state) {
            1 -> {
                context.resources.getColor(R.color.green, null)
            }
            2 -> {
                context.resources.getColor(R.color.red, null)
            }
            else -> {
                context.resources.getColor(R.color.red, null)

            }
        }
    }

    /**
     * 识别结果显示
     */
    fun getIdentificationTextSting(it: StatueResult): String {
        return when (it.state) {
            1 -> {
                context.getString(R.string.identification_success)
            }
            2 -> {
                context.getString(R.string.identification_failure)
            }
            else -> {
                ""
            }
        }
    }


    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (USBHelper.ACTION_USB_PERMISSION == action) {
                // 到此你的android设备已经连上zigbee设备
                statueOpen.postValue(StatueOpen(3, 0))
            } else {
                statueOpen.postValue(StatueOpen(3, -1))

            }
        }
    }


    fun intiIcCard(icCardCallBack: IcCardCallBack) {
        if (mIcCardUtils == null)
            mIcCardUtils = IcCardUtils()
        mIcCardUtils?.init(context, icCardCallBack)
    }

    fun icCardRead(icCardReadCallBack: IcCardReadCallBack) {
        mIcCardUtils?.icCardRead(icCardReadCallBack)
    }

    fun onDestroyIcCard() {
        mIcCardUtils?.onDestroy()
    }

    fun uploadTestFiles(myLogInfo: MyLogInfo) {
        val filePath = myLogInfo.filePath
        val file = File(filePath)
        if (file.exists()) {
            val body: RequestBody = requestBody(myLogInfo, file, requestBody(file))

            launchOnlyresult({
                dataRepository.uploadTestFiles(body)!!
            }, {
                uploadingFileResult.postValue(0)
                JUtils.onToastLong(R.string.upload_successful)

            }, {
                uploadingFileResult.postValue(1)
                JUtils.onToastLong(R.string.fail_to_upload)

            }, {}, true)
        } else {
            JUtils.onToastLong(R.string.file_no_exists)
        }


    }

    private fun requestBody(
        myLogInfo: MyLogInfo,
        file: File,
        fileRQ: RequestBody
    ): RequestBody {
        LogUtils.i(TAG, GoSonUtils.toJson(myLogInfo), 0, 0)
        return MultipartBody.Builder().apply {
            addFormDataPart("mac", myLogInfo.mac)
            addFormDataPart("sn", myLogInfo.sn)
            addFormDataPart("reportTime", myLogInfo.reportTime.toString())
            addFormDataPart("employeeNo", myLogInfo.employeeNo)
            addFormDataPart("itemName", myLogInfo.itemName)
            addFormDataPart("terminalInfo", myLogInfo.terminalInfo)
            addFormDataPart("file", file.name, fileRQ)
        }.build()//可以任意调用该对象的任意方法，并返回该对象

    }

    private fun requestBody(file: File): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
    }
}