package com.eloam.process.utils

import android.annotation.SuppressLint
import android.content.Context
import com.eloam.process.callBack.IcCardCallBack
import com.eloam.process.callBack.IcCardReadCallBack
import com.eloam.process.ui.WelcomeActivity
import com.imagpay.Settings
import com.imagpay.SwipeEvent
import com.imagpay.SwipeListener
import com.imagpay.emv.*
import com.imagpay.enums.CardDetected
import com.imagpay.enums.PrintStatus
import com.imagpay.usb.USBConstants
import com.imagpay.usb.UsbHandler
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class IcCardUtils {
    companion object {
        private const val TAG = "IcCardUtils"
    }

    private var handler: UsbHandler? = null
    private var setting: Settings? = null

    fun init(context: Context, icCardCallBack: IcCardCallBack) {
        handler = UsbHandler(context)
        setting = Settings(handler)
        // for emv chip card test
        handler!!.isShowAPDU = true
        connect(icCardCallBack)
        // for some phones that can detect usb insert
        // _handler.setAutoConn(true);
        handler!!.addSwipeListener(object : SwipeListener {
            override fun onStopped(event: SwipeEvent) {
                LogUtils.i(TAG, "onStopped ${event.value}", WelcomeActivity.UPLOADING_TIME, 1)

            }

            override fun onStarted(event: SwipeEvent) {
                LogUtils.i(TAG, "onStarted ${event.value}", WelcomeActivity.UPLOADING_TIME, 1)

            }

            override fun onReadData(event: SwipeEvent) {
                LogUtils.i(TAG, "onReadData ${event.value}", WelcomeActivity.UPLOADING_TIME, 1)

            }

            override fun onParseData(event: SwipeEvent) {
                LogUtils.i(TAG, "onParseData ${event.value} ${event.type}", WelcomeActivity.UPLOADING_TIME, 1)

            }

            override fun onDisconnected(event: SwipeEvent) {
                LogUtils.i(TAG, "onDisconnected ${event.value}", WelcomeActivity.UPLOADING_TIME, 1)

            }

            override fun onConnected(event: SwipeEvent) {
                LogUtils.i(TAG, "onConnected ${event.value}", WelcomeActivity.UPLOADING_TIME, 1)

            }

            override fun onPermission(event: SwipeEvent) {

            }

            override fun onCardDetect(arg0: CardDetected) {

            }

            override fun onPrintStatus(arg0: PrintStatus) {

            }
        })
    }

    private fun connect(icCardCallBack: IcCardCallBack) {
        val connect = (handler as UsbHandler).connect()
        LogUtils.i(TAG, "IC Card connect code== $connect", WelcomeActivity.UPLOADING_TIME, 1)
        when (connect) {
            USBConstants.USB_NO_PERMISSION -> {
                handler!!.checkPermission()
                icCardCallBack.onFailure()
                LogUtils.i(TAG, "IC Card connect code== $connect", WelcomeActivity.UPLOADING_TIME, 1)

            }

            USBConstants.USB_STATUS_OK -> {
                icCardCallBack.onSuccess()
            }
            else -> {
//                icCardCallBack.onFailure()
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun icCardRead(icCardReadCallBack: IcCardReadCallBack) {
        // ic
        val param = EMVParam()
        param.slot = 0x00.toByte()
        // param.setReadOnly(true);
        // if call setReadOnly(true), SDK will only read card data
        // if call setReadOnly(false), SDK will read card data and verify pin
        // and submit data
        // 商户名
        param.merchName = "4368696E61" // hex string of china
        // 商户类别码
        param.merchCateCode = "0001"
        // 商户标识(商户号)
        param.merchId = "313233343536373839303132333435"
        // 终端标识(POS号)
        param.termId = "3132333435363738"
        // 终端类型
        param.terminalType = 0x22.toByte()
        // 终端性能
        param.capability = "E0F8C8"
        // param.setCapability("E028C8");//do not support pin
        // 终端扩展性能
        param.exCapability = "F00000A001"
        // 交易货币代码指数
        param.transCurrExp = 0x02.toByte()
        // 终端国家代码
        param.countryCode = "0840"
        // 交易货币代码
        param.transCurrCode = "0840"
        // 当前交易类型
        param.transType =
            0x00.toByte() // EMVConstants.TRANS_TYPE_GOODS/EMVConstants.TRANS_TYPE_CASH/EMVConstants.TRANS_TYPE_CASHBACK
        // IFD序列号
        param.termIFDSn = "3838383838383838" // SN is 88888888
        // 授权金额
        param.authAmnt = 8000000 // transaction amount
        // 其他金额
        param.otherAmnt = 0
        val date = Date()
        var sdf: DateFormat = SimpleDateFormat("yyMMdd")
        // 交易日期指针(3 BYTE)
        param.transDate = sdf.format(date)
        sdf = SimpleDateFormat("HHmmss")
        // 交易时间指针(3 BYTE)
        param.transTime = sdf.format(date)
        // FIME parameters(MasterCard Test Card), if other card type, need to
        // change.
        loadMasterCardCapks(param)
        loadMasterCardRevocs(param)
        // Visa
        loadVisaRevocs(param)
        loadChinaAIDs(param)
        setting!!.icReset()
        handler!!.kernelInit(param)
        handler!!.process()
        handler!!.icOff()
        try {
            val data = handler!!.getTLVDataByTag(0x5a)
            icCardReadCallBack.onSuccess(data)
            LogUtils.d(TAG, "Ic card id onSuccess $data", WelcomeActivity.UPLOADING_TIME, 1)
        } catch (E: Exception) {
            LogUtils.d(
                TAG,
                "Ic card id onSuccess null${E.message}",
                WelcomeActivity.UPLOADING_TIME,
                1
            )
            icCardReadCallBack.onSuccess("null")
        }


    }

    private fun loadChinaAIDs(ep: EMVParam) {
        // PBOC_TEST_APP
        val ea = EMVApp()
        ea.appName = ""
        ea.aid = "A0000003330101"
        ea.selFlag = EMVConstants.PART_MATCH
        ea.priority = 0x00.toByte()
        ea.targetPer = 0x00.toByte()
        ea.maxTargetPer = 0x00.toByte()
        ea.floorLimitCheck = 0x01.toByte()
        ea.floorLimit = 2000
        ea.threshold = 0x00
        ea.tacDenial = "0000000000"
        ea.tacOnline = "0000001000"
        ea.tacDefault = "0000000000"
        ea.acquierId = "000000123456"
        ea.ddol = "039F3704"
        ea.tdol = "0F9F02065F2A029A039C0195059F3704"
        ea.version = "0096"
        ep.addApp(ea)
    }

    private fun loadMasterCardCapks(ep: EMVParam) {
        // FE
        var ec = EMVCapk()
        ec.rid = "A000000004"
        ec.keyID = 0xFE.toByte()
        ec.modul =
            ("A653EAC1C0F786C8724F737F172997D63D1C3251C4" + "4402049B865BAE877D0F398CBFBE8A6035E24AFA08" + "6BEFDE9351E54B95708EE672F0968BCD50DCE40F78" + "3322B2ABA04EF137EF18ABF03C7DBC5813AEAEF3"
                    + "AA7797BA15DF7D5BA1CBAF7FD520B5A482D8D3FE" + "E105077871113E23A49AF3926554A70FE10ED728CF793B62A1")
        ec.exponent = "03"
        ec.expDate = "491231" // YYMMDD
        ec.checkSum = "9A295B05FB390EF7923F57618A9FDA2941FC34E0"
        ep.addCapk(ec)
        // F3
        ec = EMVCapk()
        ec.rid = "A000000004"
        ec.keyID = 0xF3.toByte()
        ec.modul =
            ("98F0C770F23864C2E766DF02D1E833DFF4FFE92D696E" + "1642F0A88C5694C6479D16DB1537BFE29E4FDC6E6E8AFD1B0EB7EA012" + "4723C333179BF19E93F10658B2F776E829E87DAEDA9C94A8B3382199A3"
                    + "50C077977C97AFF08FD11310AC950A72C3CA5002EF513FCCC286E646E3C" + "5387535D509514B3B326E1234F9CB48C36DDD44B416D23654034A66F403BA511C5EFA3")
        ec.exponent = "03"
        ec.expDate = "491231" // YYMMDD
        ec.checkSum = "A69AC7603DAF566E972DEDC2CB433E07E8B01A9A"
        ep.addCapk(ec)
        // F8
        ec = EMVCapk()
        ec.rid = "A000000004"
        ec.keyID = 0xF8.toByte()
        ec.modul =
            ("A1F5E1C9BD8650BD43AB6EE56B891EF7459C0A24FA8" + "4F9127D1A6C79D4930F6DB1852E2510F18B61CD354DB83A356BD19" + "0B88AB8DF04284D02A4204A7B6CB7C5551977A9B36379CA3DE1A08E"
                    + "69F301C95CC1C20506959275F41723DD5D2925290579E5A95B0DF632" + "3FC8E9273D6F849198C4996209166D9BFC973C361CC826E1")
        ec.exponent = "03"
        ec.expDate = "491231" // YYMMDD
        ec.checkSum = "F06ECC6D2AAEBF259B7E755A38D9A9B24E2FF3DD"
        ep.addCapk(ec)
        // FA
        ec = EMVCapk()
        ec.rid = "A000000004"
        ec.keyID = 0xFA.toByte()
        ec.modul =
            ("A90FCD55AA2D5D9963E35ED0F440177699832F49C6" + "BAB15CDAE5794BE93F934D4462D5D12762E48C38BA83D8445DEAA" + "74195A301A102B2F114EADA0D180EE5E7A5C73E0C4E11F67A43DDA"
                    + "B5D55683B1474CC0627F44B8D3088A492FFAADAD4F42422D0E70135" + "36C3C49AD3D0FAE96459B0F6B1B6056538A3D6D44640F94467B10886" + "7DEC40FAAECD740C00E2B7A8852D")
        ec.exponent = "03"
        ec.expDate = "491231"
        ec.checkSum = "5BED4068D96EA16D2D77E03D6036FC7A160EA99C"
        ep.addCapk(ec)
        // EF
        ec = EMVCapk()
        ec.rid = "A000000004"
        ec.keyID = 0xEF.toByte()
        ec.modul =
            ("A191CB87473F29349B5D60A88B3EAEE0973AA6F1A08" + "2F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293" + "EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A1"
                    + "3ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA" + "565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B" + "8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35"
                    + "C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB65" + "1AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC" + "6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3B" + "7DEC40FAAECD740C00E2B7A8852D")
        ec.exponent = "03"
        ec.expDate = "491231" // YYMMDD
        ec.checkSum = "21766EBB0EE122AFB65D7845B73DB46BAB65427A"
        ep.addCapk(ec)
        // F1
        ec = EMVCapk()
        ec.rid = "A000000004"
        ec.keyID = 0xF1.toByte()
        ec.modul =
            ("A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C" + "5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C5" + "0AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02D"
                    + "D1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF9885" + "1FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E0906429" + "09C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1" + "B43B3D7835088A2FAFA7BE7")
        ec.exponent = "03"
        ec.expDate = "491231" // YYMMDD
        ec.checkSum = "D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB"
        ep.addCapk(ec)
    }

    private fun loadVisaRevocs(ep: EMVParam) {
        val er = EMVRevoc()
        er.ucrid = "A000000003"
        er.ucIndex = 0x50.toByte()
        er.ucCertSn = "024455"
        ep.addRecov(er)
    }

    private fun loadMasterCardRevocs(ep: EMVParam) {
        val er = EMVRevoc()
        er.ucrid = "A000000004"
        er.ucIndex = 0xFE.toByte()
        er.ucCertSn = "082355"
        ep.addRecov(er)
    }

    fun onDestroy() {
        handler?.onDestroy()
    }
}