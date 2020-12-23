package com.eloam.process.utils

import android.content.Context
import android.media.SoundPool
import com.eloam.process.R

/**
 * @author: lico
 * @create：2020/5/21
 * @describe：
 */
object SoundUtil {


    var readSuccess: Int? = null
    var readTips: Int? = null
    var validateSuccess: Int? = null
    var validateFail: Int? = null
    var success: Int? = null
    var retry: Int? = null
    var faceCamera: Int? = null

    /**
     * 加载语音文件
     */
    suspend fun loadSoundRes(context: Context, soundPool: SoundPool) {
        //咚
        readSuccess = soundPool.load(context, R.raw.readcard_success, 1)
        //请将身份证放置于阅读器上
        readTips = soundPool.load(context, R.raw.idcardread, 1)
        //请正对摄像头
        faceCamera = soundPool.load(context, R.raw.face_camera, 1)
        //请重试
        retry = soundPool.load(context, R.raw.retry, 1)
        //比对成功
        validateSuccess = soundPool.load(context, R.raw.validate_success, 1)
        //比对失败
        validateFail = soundPool.load(context, R.raw.validate_fail, 1)
    }

    /**
     * 1: 读卡成功   2：请将身份证放置到阅读器上  3：请正对摄像头   4：比对失败 5：比对成功  6：请重试
     */
    fun playSound(soundPool: SoundPool, index: Int) {
        when (index) {
            1 -> soundPool.play(readSuccess!!, 1f, 1f, 1, 0, 1f)
            2 -> soundPool.play(readTips!!, 1f, 1f, 1, 0, 1f)
            3 -> soundPool.play(faceCamera!!, 1f, 1f, 1, 0, 1f)
            4 -> soundPool.play(validateFail!!, 1f, 1f, 1, 0, 1f)
            5 -> soundPool.play(validateSuccess!!, 1f, 1f, 1, 0, 1f)
            6 -> soundPool.play(retry!!, 1f, 1f, 1, 0, 1f)
        }
    }
}