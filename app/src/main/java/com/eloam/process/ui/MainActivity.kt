package com.eloam.process.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.eloam.process.R
import com.eloam.process.callBack.*
import com.eloam.process.data.entity.StatueAnim
import com.eloam.process.data.entity.StatueOpen
import com.eloam.process.data.entity.StatueOpenDevice
import com.eloam.process.data.entity.StatueResult
import com.eloam.process.dialog.HintDialog
import com.eloam.process.dialog.SweetAlertDialog.Companion.NORMAL_TYPE
import com.eloam.process.utils.*
import com.eloam.process.viewmodels.MainViewModel
import com.example.scarx.idcardreader.utils.IdCardRenderUtils
import com.example.scarx.idcardreader.utils.imp.MyCallBack
import com.face.cn.ImageStack
import com.face.sweepplus.data.`interface`.HintDialogBtnListener
import com.serenegiant.usb.IFrameCallback
import com.serenegiant.usb.USBMonitor
import com.zkteco.android.biometric.core.device.ParameterHelper
import com.zkteco.android.biometric.core.device.TransportType
import com.zkteco.android.biometric.core.utils.LogHelper
import com.zkteco.android.biometric.core.utils.ToolUtils
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.top_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.lico.core.base.BaseActivity
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch

@Suppress("DEPRECATION")
class MainActivity : BaseActivity() {
    companion object IntentOptions {
        const val TAG = "MainActivity"
        const val RGB_CAMERA = "6684" //双目彩色摄像头
        const val IR_CAMERA = "6685"//双目红外摄像头
        const val HIGH_CAMERA = "1020"//高拍仪摄像头
        const val WIGHT = 640//高拍仪副摄像头
        const val HEIGHT = 480//高拍仪副摄像头
        private const val STORAGE_HINT_NUMBER = 100 //执行多少次数检测内存提醒

        var Intent.listCode by IntegerArrayListExtra()
        var Intent.number by IntegerExtra()

        fun startIntent(context: Context, mutableList: ArrayList<Int>, number: Int) {
            val intent = Intent(context, MainActivity::class.java)
            intent.listCode = mutableList
            intent.number = number
            context.startActivity(intent)

        }

    }


    private var mUSBMonitor: USBMonitor? = null
    private var context: Context? = null
    private val imgKjStackIrCamera = ImageStack(WIGHT, HEIGHT)
    private val imgKjStackHighCamera = ImageStack(WIGHT, HEIGHT)
    private val imgKjStackRgbCamera = ImageStack(WIGHT, HEIGHT)

    private val idCardReaderUtils: IdCardRenderUtils = IdCardRenderUtils()
    private var mFingerPrintsUtils: FingerPrintsUtils? = null
    private var isOpenHighCamera = false
    private var isOpenIrCamera = false
    private var isOpenRgbCamera = false
    private var isOpenQrCode = false
    private var isOpenFingerprints = false
    private var isOpenIdCard = false
    private var isOpenIcCard = false
    private var isFlag = true//是否开启自动测试
    private var isRunFinish = false//是否正在执行

    private var storageHintTime = 1//累计次数
    private var mCurNumber = 0
    private var mAllNumber = 10000
    private lateinit var listCode: ArrayList<Int>
    private val mainViewModel: MainViewModel by viewModel()

    override fun layoutId() = R.layout.activity_main

    init {
        context = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initData() {
        getIntentDta(intent)
        register()
        intiView()
        addObserver()
        showView()
        LogUtils.i(TAG, getDeviceSN())
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceSN(): String? {
        return Build.SERIAL
    }

    private fun getIntentDta(intent: Intent) = with(IntentOptions) {
        listCode = intent.listCode
        mAllNumber = intent.number

    }

    /**
     * 显示需要展示的View
     */
    private fun showView() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            for (code in listCode) {
                mainViewModel.statueVisibility.postValue(code)
                delay(200)
            }
            mainViewModel.statueAnim.postValue(StatueAnim(listCode[0], 1))
        }
    }

    private fun intiView() {
        backIv.setImageResource(R.drawable.ic_back)
        setNumberText()
        backIv.setOnClickListener {
            onBackIv()
        }
        startTv.setOnClickListener {
            onStartTv()
        }
    }

    private fun onBackIv() {
        if (isFlag) {
            JUtils.onToastLong(getString(R.string.stop_for))
        } else {
            finish()
        }
    }

    private fun onStartTv() {
        if (isFlag) {
            startTv.text = getString(R.string.start)
            isFlag = false
        } else {
            if (!isRunFinish) {
                JUtils.onToastLong(getString(R.string.is_run_finish))
                return
            }

            isFlag = true
            clearCurNumber()
            postNext()
            startTv.text = getString(R.string.stop)
        }


    }

    private fun clearCurNumber() {
        val number = mCurNumber / listCode.size
        LogUtils.i(TAG, "$number")
        if (number >= mAllNumber) {
            mCurNumber = 0
        }
    }

    private fun setNumberText() {
        val forNumber = getString(R.string.for_number)
        val curNumber = getString(R.string.cur_number)
        val text =
            "$forNumber$mAllNumber$curNumber<font color=\"#0D76DE\">${mCurNumber / listCode.size}</font>)"
        number_tv.text = Html.fromHtml(text)
        LogUtils.i(TAG, "mCurNumber==${mCurNumber / listCode.size}")
    }


    private fun initMonitorListener() {
        mUSBMonitor =
            UVCCameraUtil.initUSBMonitor(mContext, object : UVCCameraUtil.OnMyDevConnectListener {
                override fun onConnectDev(
                    device: UsbDevice,
                    ctrlBlock: USBMonitor.UsbControlBlock
                ) {
                    val pid = String.format("%x", device.productId)
                    LogUtils.d(TAG, "connect: $pid")
                    when (pid) {
                        RGB_CAMERA -> {
                            openRgbCamera(ctrlBlock)
                        }

                        IR_CAMERA -> {
                            openIRCamera(ctrlBlock)
                        }

                        HIGH_CAMERA -> {//
                            openHighCamera(ctrlBlock)
                        }

                        else -> {
                            openDeputyCamera(ctrlBlock)
                        }

                    }
                }
            })
    }

    private fun openDeputyCamera(ctrlBlock: USBMonitor.UsbControlBlock) {
        UVCCameraUtil.openDeputyCamera(
            mainTexture,
            ctrlBlock,
            IFrameCallback {}, object : OpenStateCallBack {
                override fun failure() {
                    next()
                }

                override fun success() {
                }
            })
    }

    private fun openHighCamera(ctrlBlock: USBMonitor.UsbControlBlock) {
        UVCCameraUtil.openHighCamera(mainTexture, ctrlBlock, IFrameCallback {
            imgKjStackHighCamera.pushImageInfo(it, System.currentTimeMillis())

        }, object : OpenStateCallBack {
            override fun failure() {
                next()
            }

            override fun success() {
                setIsOpen(0, true)
                postDeviceState(isOpenHighCamera)
                getPullImageInfo(imgKjStackHighCamera, 0)
            }
        })
    }

    private fun openIRCamera(ctrlBlock: USBMonitor.UsbControlBlock) {
        UVCCameraUtil.openIRCamera(binocularBlackTexture, ctrlBlock, IFrameCallback {
            imgKjStackIrCamera.pushImageInfo(it, System.currentTimeMillis())

        }, object : OpenStateCallBack {
            override fun failure() {
                next()
            }

            override fun success() {
                setIsOpen(2, true)
                postDeviceState(isOpenIrCamera)
                getPullImageInfo(imgKjStackIrCamera, 2)
            }
        })
    }

    private fun openRgbCamera(ctrlBlock: USBMonitor.UsbControlBlock) {
        UVCCameraUtil.openRGBCamera(binocularColorTexture, ctrlBlock, IFrameCallback {

            imgKjStackRgbCamera.pushImageInfo(it, System.currentTimeMillis())

        }, object : OpenStateCallBack {
            override fun failure() {
                next()
            }

            override fun success() {
                setIsOpen(1, true)
                postDeviceState(isOpenRgbCamera)
                getPullImageInfo(imgKjStackRgbCamera, 1)
            }
        })
    }

    /**
     * 获取图片数据
     */
    private fun getImageData(data: ByteArray, type: Int) {
        synchronized(this) {
            lifecycleScope.launch(Dispatchers.Main) {
                val bitmap = YuvUtils.nv21ToBitmap(data, WIGHT, HEIGHT)
                setImage(type, bitmap)
                val path = ImageFileUtil.saveBitmap(bitmap, type, ImageFileUtil.time)
                postResult(listCode[mCurNumber % listCode.size], getResult(File(path)), "")
                LogUtils.i(TAG, "path==$path ")
            }
        }
    }

    private fun setImage(type: Int, bitmap: Bitmap?) {
        when (type) {
            0 -> mainIv.setImageBitmap(bitmap)

            1 -> binocularColorIv.setImageBitmap(bitmap)

            2 -> binocularBlackIv.setImageBitmap(bitmap)
        }
    }

    /**
     * 显示结果
     */
    private fun postResult(code: Int, result: Int, card: String) {
        mainViewModel.statueResult.postValue(
            StatueResult(
                code,
                result,
                card
            )
        )
    }

    private fun getResult(file: File) = if (file.exists()) 1 else 2


    private fun onDestroyUvc() {
        mUSBMonitor?.unregister()
        mUSBMonitor?.destroy()
        mUSBMonitor = null
    }


    /**
     * statue : 0高拍仪，1副摄像头，2双目彩色摄像头，3双目红外摄像头，4.二维码，5二代身份证读取，6.指纹仪，7.ic卡读取
     *
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private fun addObserver() {
        openState()
        statueVisibility()
        statue()
        statueAnim()
        statueResult()
    }

    private fun statueResultNext(state: Int) {
        if (state == 1 || state == 2) {
            next()
        }
    }

    private fun statueResult() {
        mainViewModel.statueResult.observe(this, androidx.lifecycle.Observer {
            when (it.type) {
                0 -> {
                    main_camera_result_tv.text = mainViewModel.getTextSting(it)
                    clearImage(it.state, mainIv)
                    statueResultNext(it.state)
                }

                1 -> {
                    binocular_color_result_tv.text = mainViewModel.getTextSting(it)
                    clearImage(it.state, binocularColorIv)
                    statueResultNext(it.state)
                }

                2 -> {
                    binocular_black_result_tv.text = mainViewModel.getTextSting(it)
                    clearImage(it.state, binocularBlackIv)
                    statueResultNext(it.state)
                }

                3 -> {
                    qr_code_result_tv.text = if (it.state == 1) it.data else ""
                    statueResultNext(it.state)
                }

                4 -> {
                    id_card_result_tv.text = mainViewModel.getIdentificationTextSting(it)
                    statueResultNext(it.state)
                }

                5 -> {
                    fingerprints_result_tv.text = mainViewModel.getIdentificationTextSting(it)
                    statueResultNext(it.state)
                }

                6 -> {
                    ic_card_result_tv.text = if (it.state == 1) it.data else ""
                    statueResultNext(it.state)
                }

                else -> {

                }
            }

        })
    }

    private fun clearImage(state: Int, imageView: AppCompatImageView) {
        if (state == 0) {
            imageView.setImageBitmap(null)
        }
    }

    private fun statueAnim() {
        mainViewModel.statueAnim.observe(this, androidx.lifecycle.Observer {
            when (it.type) {
                0 -> {
                    setLoadingView(it, main_camera_fl, main_camera_iv, mainCameraAnim)
                }

                1 -> {
                    setLoadingView(it, binocular_color_fl, binocular_color_iv, binocular_color_anim)
                }

                2 -> {
                    setLoadingView(it, binocular_black_fl, binocular_black_iv, binocular_black_anim)
                }

                3 -> {
                    setLoadingView(it, qr_code_fl, qr_code_iv, qr_code_anim)
                }

                4 -> {
                    setLoadingView(it, id_card_fl, id_card_iv, id_card_anim)
                }

                5 -> {
                    setLoadingView(it, fingerprints_fl, fingerprints_iv, fingerprints_anim)
                }

                6 -> {
                    setLoadingView(it, ic_card_fl, ic_card_iv, ic_card_anim)

                }

                else -> {

                }
            }

        })
    }

    /**
     *
     * 开始显示View
     */
    private fun setLoadingView(
        it: StatueAnim,
        camera_fl: FrameLayout,
        camera_iv: AppCompatImageView,
        cameraAnim: LottieAnimationView
    ) {

        when (it.state) {
            0 -> {
                camera_fl.visibility = View.INVISIBLE
                camera_iv.visibility = View.GONE
                cameraAnim.visibility = View.GONE
            }
            1 -> {
                camera_fl.visibility = View.VISIBLE
                cameraAnim.visibility = View.VISIBLE
                cameraAnim.playAnimation()
                postStateOpen(listCode[mCurNumber % listCode.size], 1)
                isRunFinish = false
            }
            else -> {
                cameraAnim.cancelAnimation()
                cameraAnim.visibility = View.GONE
                camera_iv.visibility = View.VISIBLE
            }
        }
    }

    private fun postStateOpen(code: Int, state: Int) {
        mainViewModel.statueOpen.postValue(StatueOpen(code, state))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun statue() {
        mainViewModel.statueOpenDevice.observe(this, androidx.lifecycle.Observer {
            when (it.type) {
                0 -> {
                    main_camera_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    main_camera_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                1 -> {
                    binocular_color_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    binocular_color_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                2 -> {
                    binocular_black_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    binocular_black_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                3 -> {
                    qr_code_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    qr_code_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                4 -> {
                    id_card_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    id_card_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                5 -> {
                    fingerprints_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    fingerprints_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                6 -> {
                    ic_card_state_tv.text = mainViewModel.getOpenDeviceTextSting(it)
                    ic_card_state_tv.setTextColor(mainViewModel.getOpenDeviceTextColor(it))
                }

                else -> {

                }
            }

        })
    }


    private fun statueVisibility() {
        mainViewModel.statueVisibility.observe(this, androidx.lifecycle.Observer {

            when (it) {
                0 -> {
                    main_camera_ll.visibility = View.VISIBLE
                    mainTexture.visibility = View.VISIBLE
                    mainIv.visibility = View.VISIBLE
                }

                1 -> {
                    binocular_color_ll.visibility = View.VISIBLE
                    binocularColorTexture.visibility = View.VISIBLE
                    binocularColorIv.visibility = View.VISIBLE
                }

                2 -> {
                    binocular_black_ll.visibility = View.VISIBLE
                    binocularBlackTexture.visibility = View.VISIBLE
                    binocularBlackIv.visibility = View.VISIBLE
                }

                3 -> qr_code_ll.visibility = View.VISIBLE

                4 -> id_card_ll.visibility = View.VISIBLE

                5 -> fingerprints_ll.visibility = View.VISIBLE

                6 -> ic_card_ll.visibility = View.VISIBLE

                else -> {
                }
            }

        })
    }

    private fun openState() {
        mainViewModel.statueOpen.observe(this, androidx.lifecycle.Observer {
            when (it.type) {
                0 -> {
                    saveOrRequestPermission(isOpenHighCamera, imgKjStackHighCamera, 0, HIGH_CAMERA)
                }

                1 -> {
                    saveOrRequestPermission(isOpenRgbCamera, imgKjStackRgbCamera, 1, RGB_CAMERA)
                }

                2 -> {
                    saveOrRequestPermission(isOpenIrCamera, imgKjStackIrCamera, 2, IR_CAMERA)
                }

                3 -> {
                    openQrCode(it)
                }

                4 -> {
                    openOrIntiIdCard(it)

                }
                5 -> {
                    openFingerPrints()

                }

                6 -> {
                    onIcCard()
                }

                else -> {

                }
            }

        })
    }

    private fun openFingerPrints() {
        lifecycleScope.launch {
            if (isOpenFingerprints) {
                openReadChar()
            } else {
                openUsbDevices()
            }
        }


    }

    /**
     * 开启指纹识别功能
     */
    private suspend fun openUsbDevices() {
        if (mFingerPrintsUtils == null)
            mFingerPrintsUtils = FingerPrintsUtils()

        mFingerPrintsUtils?.openUsbDevices(object : OpenFingerprintsCallBack {
            override fun failure() {
                isOpenFingerprints = false
                postDeviceState(false)
                next()
            }

            override fun success() {
                isOpenFingerprints = true
                postDeviceState(true)
                openReadChar()
            }
        })
    }

    /**
     * 开启指纹读取
     */
    private fun openReadChar() {
        lifecycleScope.launch(Dispatchers.IO) {
            mFingerPrintsUtils?.readChar(object : FingerprintsCallBack {
                override fun onSuccess(string: String) {
                    postResult(listCode[mCurNumber % listCode.size], 1, "")

                }

                override fun onFailure() {
                    postResult(listCode[mCurNumber % listCode.size], 2, "")

                }
            })
        }
    }

    private fun onIcCard() {
        if (isOpenIcCard) {
            icCardRead()
        } else {
            intiIcCard()
        }
    }

    private fun intiIcCard() {
        mainViewModel.intiIcCard(object : IcCardCallBack {
            override fun onSuccess() {
                isOpenIcCard = true
                postDeviceState(true)
                icCardRead()
            }

            override fun onFailure() {
                postDeviceState(false)
                next()

            }
        })
    }

    private fun icCardRead() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            mainViewModel.icCardRead(object : IcCardReadCallBack {
                override fun onSuccess(icCard: String) {
                    postResult(listCode[mCurNumber % listCode.size], 1, icCard)
                }
            })
        }

    }


    private fun openQrCoed() {
        if (isOpenQrCode) {
            qr_code_edt.isFocusable = true
            qr_code_edt.isFocusableInTouchMode = true
        } else {
            postDeviceState(true)
            qr_code_edt.isFocusable = true
            qr_code_edt.isFocusableInTouchMode = true
        }
    }

    private fun openQrCode(it: StatueOpen) {
        when (it.state) {
            1 -> {
                openQrCoed()
            }
            2 -> {

            }
        }
    }


    private fun openOrIntiIdCard(it: StatueOpen) {
        if (it.state == 1) {
            if (isOpenIcCard) {
                openIdCard()
            } else {
                mainViewModel.initIdCardUsbDev()
            }
        } else if (it.state == 2) {
            idCardReaderUtils.setbStoped(true)
        } else if (it.state == 0) {
            isOpenIcCard = true
            postDeviceState(true)
            openIdCard()
        } else if (it.state == -1) {
            postDeviceState(false)
            next()

        }
    }

    /**
     * 开启身份证识别
     */
    private fun openIdCard() {
        if (idCardReaderUtils.isbStoped()) {
            idCardReaderUtils.setbStoped(false)
        } else {
            startIdCardRead(idCardReaderUtils)
        }

    }


    private fun saveOrRequestPermission(
        isOpen: Boolean,
        imageStack: ImageStack,
        type: Int,
        pId: String
    ) {
        if (isOpen) {
            postDeviceState(isOpen)
            getPullImageInfo(imageStack, type)
        } else {
            if (!requestPermission(pId)) {
                postDeviceState(isOpen)
                next()
            }

        }
    }

    private fun setIsOpen(type: Int, boolean: Boolean) {
        when (type) {
            0 -> isOpenHighCamera = boolean

            1 -> isOpenRgbCamera = boolean

            2 -> isOpenIrCamera = boolean

            3 -> isOpenQrCode = boolean

            4 -> isOpenIdCard = boolean

            5 -> isOpenFingerprints = boolean

            6 -> isOpenIcCard = boolean

        }

    }

    private fun next() {
        postStateValue(listCode[mCurNumber % listCode.size], 2)
        isRunFinish = true
        mCurNumber++
        if (isFlag) {
            postNext()
        }

    }

    private fun getPullImageInfo(imageStack: ImageStack, type: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            val imgInfoKj = imageStack.pullImageInfo()
            LogUtils.d(TAG, "getPullImageInfo ${imgInfoKj.isNew}")
            if (imgInfoKj.isNew) {
                getImageData(imgInfoKj.data, type)
            } else {
                next()
            }
        }
    }

    /**
     * 执行下一个操作
     */
    private fun postNext() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(2000)
            nextAsLast()
            postStateValue(listCode[mCurNumber % listCode.size], 1)

        }

    }


    /**
     * 执行到最后一项，清空数据，显示运行次数结果
     */
    private suspend fun nextAsLast() {
        LogUtils.d(TAG, "nextAsLast ${mCurNumber % listCode.size} ")
        if (mCurNumber % listCode.size == 0) {
            setNumberText()
            stopNext()
            checkStorage()
            postCheckResult()
        }
    }

    /**
     * 执行完了，停止
     */
    private fun stopNext() {
        if (mCurNumber / listCode.size >= mAllNumber) {
            isFlag = false
            startTv.text = getString(R.string.start)
        }
    }

    private suspend fun postCheckResult() {
        for (code in listCode) {
            postStateValue(code, 0)
            delay(100)
            postResult(code, 0, "")
            delay(100)
        }
        delay(2000)
    }

    private fun checkStorage() {
        if (mCurNumber / listCode.size == STORAGE_HINT_NUMBER * storageHintTime) {
            storageHintTime++
            storageHint()
        }
    }

    private fun postStateValue(code: Int, state: Int) {
        mainViewModel.statueAnim.postValue(StatueAnim(code, state))
    }

    /**
     * 显示操作结果结果
     */
    private fun postDeviceState(isOpen: Boolean) {
        mainViewModel.statueOpenDevice.postValue(
            StatueOpenDevice(
                listCode[mCurNumber % listCode.size],
                if (isOpen) 1 else 2
            )
        )
    }

    private fun requestPermission(pId: String): Boolean {
        return UVCCameraUtil.requestPermission(
            mContext,
            pId,
            500,
            mUSBMonitor!!
        )
    }


    private fun register() {
        initMonitorListener()
        mUSBMonitor?.register()
    }


    private fun startIdCardRead(idCardReaderUtils: IdCardRenderUtils) {
        LogHelper.setLevel(8) //身份证打印等级
        val idParams = HashMap<String, Any>()
        idParams[ParameterHelper.PARAM_KEY_VID] = 1024
        idParams[ParameterHelper.PARAM_KEY_PID] = 50010
        val idCardReader = IDCardReaderFactory.createIDCardReader(
            ToolUtils.getApplicationContext(),
            TransportType.USB,
            idParams
        )

        idCardReaderUtils.readerIdCard(idCardReader, CountDownLatch(1), object : MyCallBack {
            override fun onSuccess(idCardInfo: IDCardInfo?) {
                LogUtils.d(TAG, "readerIdCard onSuccess")

                postResult(listCode[mCurNumber % listCode.size], 1, "")
                idCardReaderUtils.setbStoped(true)


            }

            override fun onFail(error: String?) {
                LogUtils.d(TAG, "readerIdCard onFail")

            }

            override fun onRequestDevicePermission() {

            }

            override fun onNoCards() {
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        mFingerPrintsUtils?.onDestroy()
        idCardReaderUtils.setbStoped(true)
        UVCCameraUtil.releaseHighCamera()
        UVCCameraUtil.releaseRGBCamera()
        UVCCameraUtil.releaseIRCamera()
        mainViewModel.onDestroyIcCard()
        onDestroyUvc()

    }

    /**
     * 内存超过80%提醒和SD卡不存在提醒
     */
    private fun storageHint() {
        val type = StorageUtils.readSDCard(this, true)
        LogUtils.d(WelcomeActivity.TAG, "storageHint$type")
        if (type == 1) {
            showDialog()
        }
    }

    private fun showDialog() {

        val hintDialog = HintDialog(this, object : HintDialogBtnListener {
            override fun cancel() {

            }

            override fun enter(string: String) {
                isFlag = false
                startTv.text = getString(R.string.start)

            }
        })
        hintDialog.setView(NORMAL_TYPE, getString(R.string.SD_Card_has_reached_hint))
        hintDialog.show()

    }


}




