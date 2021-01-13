package com.eloam.process.utils

import android.content.Context
import android.content.SharedPreferences
import com.eloam.process.MyApp

/**
 * 缓存工具类 ，通过SharedPreferences缓存数据
 *
 * @author:
 */
object CatchUtil {
    private var mSp: SharedPreferences? = null
    private const val SP_NAME = "com.face.sweep" //以sp存储的xml文件名
    private const val UUID = "uuid" //设备唯一标识
    private const val FACE_DETECTION = "face_detection" //人脸检测
    private const val IMAGE_SYNTHESIS = "Image_synthesis" //图片合成
    private const val KEY = "key"
    private const val URL = "url"
    private fun getSharedPreferences(): SharedPreferences {
        if (mSp == null) {
            mSp = MyApp.getApplication().getSharedPreferences(
                SP_NAME,
                Context.MODE_PRIVATE
            )
        }
        return mSp!!
    }


    fun setString(key: String, value: String) {
        val sp = getSharedPreferences()
        sp.edit().putString(key, value).apply() //提交
    }

    fun getString(key: String): String {
        val sp = getSharedPreferences()
        return sp.getString(key, "")
    }

    fun setFaceDetection(boolean: Boolean) {
        val sp = getSharedPreferences()
        sp.edit().putBoolean(FACE_DETECTION, boolean).apply() //提交
    }

    fun getFaceDetection(): Boolean {
        val sp = getSharedPreferences()
        return sp.getBoolean(FACE_DETECTION, false)
    }

    fun setImageSynthesis(boolean: Boolean) {
        val sp = getSharedPreferences()
        sp.edit().putBoolean(IMAGE_SYNTHESIS, boolean).apply() //提交
    }

    fun getImageSynthesis(): Boolean {
        val sp = getSharedPreferences()
        return sp.getBoolean(IMAGE_SYNTHESIS, true)
    }

    fun setUuid(key: String?) {
        val sp = getSharedPreferences()
        sp.edit().putString(UUID, key).apply() //提交
    }

    fun getUuid(): String? {
        val sp = getSharedPreferences()
        return sp.getString(UUID, "")
    }

    fun getKey(): String? {
        val sp = getSharedPreferences()
        return sp.getString(KEY, "")
    }

    //获取版本
    fun setKey(key: String?) {
        val sp = getSharedPreferences()
        sp.edit().putString(KEY, key).apply() //提交
    }

    fun getUrl(): String? {
        val sp = getSharedPreferences()
        return sp.getString(URL, "")
    }

    //获取版本
    fun setUrl(key: String?) {
        val sp = getSharedPreferences()
        sp.edit().putString(URL, key).apply() //提交
    }


}