/**
 * 功    能：
 * 类 列 表：LogUtils
 * 作　　者：lzp
 * 创建日期：2020/7/8 0008  下午 2:08
 * 注　　意：
 * Copyright (c) 版权所有.
 */
package com.eloam.process.utils

import android.util.Log

/**
 * 功    能：
 * 作　　者：lzp <br></br>
 * 创建日期：2016/7/8 0008  下午 2:08 <br></br>
 * 注　　意： <br></br>
 */
object LogUtils {
    private var logFlag = true
    fun isLogFlag(): Boolean {
        return logFlag
    }

    fun setLogFlag(logFlag: Boolean) {
        LogUtils.logFlag = logFlag
    }

    fun i(tag: String?, str: String?, time: Long, type: Int) {
        if (logFlag) {
            Log.i(tag, unicodeToUTF_8(str))
            MyLocalLog.i(tag!!, str!!, time, type)
        }
    }

    fun d(tag: String?, str: String?, time: Long, type: Int) {
        if (logFlag) {
            Log.d(tag, unicodeToUTF_8(str))
            MyLocalLog.d(tag!!, str!!, time, type)
        }
    }

    fun v(tag: String?, str: String?, time: Long, type: Int) {
        if (logFlag) {
            Log.v(tag, unicodeToUTF_8(str))
            MyLocalLog.v(tag!!, str!!, time, type)
        }
    }

    fun e(tag: String?, str: String?, time: Long, type: Int) {
        if (logFlag) {
            Log.e(tag, unicodeToUTF_8(str))
            MyLocalLog.e(tag!!, str!!, time, type)
        }
    }

    private fun unicodeToUTF_8(src: String?): String? {
        if (null == src) {
            return null
        }
        println("src: $src")
        val out = StringBuilder()
        var i = 0
        while (i < src.length) {
            val c = src[i]
            if (i + 6 < src.length && c == '\\' && src[i + 1] == 'u') {
                val hex = src.substring(i + 2, i + 6)
                try {
                    out.append(hex.toInt(16).toChar())
                } catch (nfe: NumberFormatException) {
                    nfe.fillInStackTrace()
                }
                i += 6
            } else {
                out.append(src[i])
                ++i
            }
        }
        return out.toString()
    }
}