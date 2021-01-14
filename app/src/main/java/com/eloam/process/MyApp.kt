package com.eloam.process

import android.app.Application
import android.content.Context
import com.eloam.process.data.ObjectBox
import com.eloam.process.koin.appModule
import com.eloam.process.utils.CrashHandler
import com.eloam.process.utils.YuvUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

/**
 * @author: lzp
 * @create：2020/8/15
 * @describe：
 */
class MyApp : Application() {

    /**
    companion object {
    val mDM2016 = SerialDM2016()
    val mDetect = FaceDetect()
    val mLive = FaceLive()
    val mRecognize = FaceRecognize()
    var mInitSuccess = false
    }*/
    companion object {
        var context: Application? = null
        fun getApplication(): Context {
            return context!!
        }

    }

    init {
        context = this
    }

    override fun onCreate() {
        super.onCreate()

        CrashHandler.instance?.init(this)
        startKoin {
            AndroidLogger()
            androidContext(this@MyApp)
            koinApplication { this@MyApp }
            modules(appModule)
        }
        YuvUtils.init(this)
        ObjectBox.init(this)

    }
    /**
    //数据库
    ObjectBox.init(this)

    //播放器配置
    VideoViewManager.setConfig(
    VideoViewConfig.newBuilder()
    .setLogEnabled(BuildConfig.DEBUG)
    .setPlayerFactory(ExoMediaPlayerFactory.create())
    .build())

    //创建目录
    FaceRecUtil.createFaceDir(this)
    //配置算法文件
    initFaceSdk(this, mDM2016, mDetect, mLive, mRecognize)

    }*/


    /**
    /**
     * 初始化配置文件
    */
    private fun initFaceSdk(
    context: Context,
    mDM2016: SerialDM2016,
    mDetect: FaceDetect,
    mLive: FaceLive,
    mRecognize: FaceRecognize
    ) {
    MainScope().launch(Dispatchers.IO) {
    FaceRecUtil.initFaceModel(context)
    mDM2016.configDevice(FaceRecUtil.DEFAULT_DM2016_SERIAL)
    //   算法鉴权
    val success = FaceRecUtil.authFace(mDM2016, mDetect, mLive, mRecognize)

    if (success) {
    //初始化算法SDK
    mInitSuccess = FaceRecUtil.initAlgorithm(mDetect, mLive, mRecognize)
    }
    }
    }
     */
}