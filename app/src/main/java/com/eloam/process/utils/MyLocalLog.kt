@file:Suppress("DEPRECATION")

package com.eloam.process.utils

import android.annotation.SuppressLint
import android.os.Environment
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Administrator on 2019/4/8.
 */
object MyLocalLog {
    private const val MYLOG_WRITE_TO_FILE = true // 日志写入文件开关  4
    private const val SDCARD_LOG_FILE_SAVE_DAYS = 0 // sd卡中日志文件的最多保存天数  7
    private const val MYLOGFILEName = "Log.txt" // 本类输出的日志文件名称  8
    @SuppressLint("SimpleDateFormat")
    private val myLogSdf =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 日志的输出格式 10
    @SuppressLint("SimpleDateFormat")
    private val logfile =
        SimpleDateFormat("yyyy-MM-dd") // 日志文件格式 11

    fun w(tag: String, msg: Any) { // 警告信息 14
        log(tag, msg.toString(), 'w')
    }

    fun e(tag: String, msg: Any) { // 错误信息 18
        log(tag, msg.toString(), 'e')
    }

    fun d(tag: String, msg: Any) { // 调试信息 22
        log(tag, msg.toString(), 'd')
    }

    fun i(tag: String, msg: Any) { //
        log(tag, msg.toString(), 'i')
    }

    fun v(tag: String, msg: Any) {
        log(tag, msg.toString(), 'v')
    }

    fun w(tag: String, text: String) {
        log(tag, text, 'w')
    }

    @JvmStatic
    fun e(tag: String, text: String) {
        log(tag, text, 'e')
    }

    @JvmStatic
    fun d(tag: String, text: String) {
        log(tag, text, 'd')
    }

    @JvmStatic
    fun i(tag: String, text: String) {
        log(tag, text, 'i')
    }

    @JvmStatic
    fun v(tag: String, text: String) {
        log(tag, text, 'v')
    }


    @JvmStatic
    fun saveNoNetworkLog(tag: String, text: String) {
        log(tag, text, 'i', "NoNetworkData")
    }


    @JvmStatic
    fun saveCrashHandler(tag: String, text: String) {
        log(tag, text, 'i', "ErrorLog")
    }

    /**
     * 54      * 根据tag, msg和等级，输出日志
     *
     * @param tag   标记
     * @param msg   消息
     * @param level 等级
     * @return void
     * @since v 1.0
     */
    private fun log(tag: String, msg: String, level: Char) {
        if (MYLOG_WRITE_TO_FILE) writeLogToFile(level.toString(), tag, msg, "MyLog")
    }

    /**
     * 54      * 根据tag, msg和等级，输出日志
     *
     * @param tag   标记
     * @param msg   消息
     * @param level 等级
     * @return void
     * @since v 1.0
     */
    private fun log(tag: String, msg: String, level: Char, fileName: String) {
        if (MYLOG_WRITE_TO_FILE) writeLogToFile(level.toString(), tag, msg, fileName)
    }

    /**
     * 81      * 打开日志文件并写入日志
     *
     * @return 84      *
     */
    private fun writeLogToFile(
        myLogType: String,
        tag: String,
        text: String,
        fileName: String
    ) {
        val nowTime = Date()
        val needWriteFile = logfile.format(nowTime)
        val needWriteMessage =
            """
            ${myLogSdf.format(nowTime)}-$myLogType-$tag-$text


            """.trimIndent()
        val filePath = SDCardUtils.getSDMouthPath(fileName)
        val file = File(filePath, needWriteFile + MYLOGFILEName)
        try {
            val filerWriter = FileWriter(file, true) // 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖 94
            val bufWriter = BufferedWriter(filerWriter)
            bufWriter.write(needWriteMessage)
            bufWriter.newLine()
            bufWriter.close()
            filerWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 105      * 删除制定的日志文件
     */
    fun delFile() { // 删除日志文件108
        val needDelFile = logfile.format(dateBefore)
        val dirPath = Environment.getExternalStorageDirectory()
        val file = File(
            dirPath,
            needDelFile + MYLOGFILEName
        ) // MYLOG_PATH_SDCARD_DIR111
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 117      * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private val dateBefore: Date
        get() {
            val nowTime = Date()
            val now = Calendar.getInstance()
            now.time = nowTime
            now[Calendar.DATE] = (now[Calendar.DATE]
                    - SDCARD_LOG_FILE_SAVE_DAYS)
            return now.time
        }
}