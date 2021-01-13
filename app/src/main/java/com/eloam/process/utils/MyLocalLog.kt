@file:Suppress("DEPRECATION")

package com.eloam.process.utils

import android.annotation.SuppressLint
import android.os.Environment
import com.eloam.process.utils.SDCardUtils.getLogFile
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
    const val MYLOGFILEName = "Log.txt" // 本类输出的日志文件名称  8

    @SuppressLint("SimpleDateFormat")
    val myLogSdf =
        SimpleDateFormat("yyyy-MM-dd HH-mm-ss") // 日志的输出格式 10

    @SuppressLint("SimpleDateFormat")
    val myLog =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 日志的输出格式 10

    @SuppressLint("SimpleDateFormat")
    private val logfile =
        SimpleDateFormat("yyyy-MM-dd") // 日志文件格式 11

    fun w(tag: String, msg: Any, time: Long, type: Int) { // 警告信息 14
        log(tag, msg.toString(), 'w', time, type)
    }

    fun e(tag: String, msg: Any, time: Long, type: Int) { // 错误信息 18
        log(tag, msg.toString(), 'e', time, type)
    }

    fun d(tag: String, msg: Any, time: Long, type: Int) { // 调试信息 22
        log(tag, msg.toString(), 'd', time, type)
    }

    fun i(tag: String, msg: Any, time: Long, type: Int) { //
        log(tag, msg.toString(), 'i', time, type)
    }

    fun v(tag: String, msg: Any, time: Long, type: Int) {
        log(tag, msg.toString(), 'v', time, type)
    }

    fun w(tag: String, text: String, time: Long, type: Int) {
        log(tag, text, 'w', time, type)
    }

    @JvmStatic
    fun e(tag: String, text: String, time: Long, type: Int) {
        log(tag, text, 'e', time, type)
    }

    @JvmStatic
    fun d(tag: String, text: String, time: Long, type: Int) {
        log(tag, text, 'd', time, type)
    }

    @JvmStatic
    fun i(tag: String, text: String, time: Long, type: Int) {
        log(tag, text, 'i', time, type)
    }

    @JvmStatic
    fun v(tag: String, text: String, time: Long, type: Int) {
        log(tag, text, 'v', time, type)
    }


    /**
     * 54      * 根据tag, msg和等级，输出日志
     *
     * @param tag   标记
     * @param msg   消息
     * @param level 等级
     * @param time 日志创建时间
     * @param type 0按照天保存当天日志，其它按照日期保存当次测试日志
     * @return void
     * @since v 1.0
     */
    private fun log(tag: String, msg: String, level: Char, time: Long, type: Int) {
        if (MYLOG_WRITE_TO_FILE) writeLogToFile(
            level.toString(),
            tag,
            msg,
            getLogFile(),
            time,
            type
        )
    }

    /**
     * 54      * 根据tag, msg和等级，输出日志
     *
     * @param tag   标记
     * @param msg   消息
     * @param level 等级
     * @param time 日志创建时间
     * @param type 0按照天保存当天日志，其它按照日期保存当次测试日志
     * @return void
     * @since v 1.0
     */
    private fun log(
        tag: String,
        msg: String,
        level: Char,
        fileName: String,
        time: Long,
        type: Int
    ) {
        if (MYLOG_WRITE_TO_FILE) writeLogToFile(level.toString(), tag, msg, fileName, time, type)
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
        fileName: String,
        time: Long,
        type: Int
    ) {
        val nowTime = Date()
        val needWriteFile: String
        needWriteFile = if (type == 0) logfile.format(nowTime) else myLogSdf.format(time)
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