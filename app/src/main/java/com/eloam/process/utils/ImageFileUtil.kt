package com.eloam.process.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.eloam.process.callBack.DownloadListener
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageFileUtil {

    private const val TAG = "ImageFileUtil"

    @SuppressLint("SimpleDateFormat")
    private val dateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 日志的输出格式 10

    private fun getName(type: Int): String {
        return when (type) {
            0 -> {
                "-Main camera"
            }

            1 -> {
                "-Binocular color"
            }
            2 -> {
                "-Binocular bw"
            }

            else -> {
                "-Main camera"
            }
        }
    }

    val time: String
        get() = dateFormat.format(Date())

    private fun getFileName(type: Int): String {
        return when (type) {
            0 -> {
                "MainCamera"
            }

            1 -> {
                "BinocularColor"
            }
            2 -> {
                "BinocularBw"
            }

            else -> {
                "MainCamera"
            }
        }

    }


    /**
     * 保存bitmap到本地
     * @param mBitmap 图片
     * @return
     */
    fun saveBitmap(
        mBitmap: Bitmap?,
        type: Int,
        time: String
    ): String {

        try {
            val savePath = SDCardUtils.getSDMouthPath(getFileName(type))
            val filePic =
                File(savePath, time.replace(":", "-") + ".jpg")
            if (!filePic.exists()) {
                filePic.createNewFile()
            }
            val fos = FileOutputStream(filePic)
            when (type) {
                2 -> {
                    mBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                }
                else -> {
                    mBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                }
            }
            fos.flush()
            fos.close()
            return filePic.path
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""

    }

    fun mergeThumbnailBitmap(firstBitmap: Bitmap, secondBitmap: Bitmap): Bitmap {
        //以其中一张图片的大小作为画布的大小，或者也可以自己自定义
        val bitmap = Bitmap.createBitmap(
            firstBitmap.width, firstBitmap
                .height * 2, firstBitmap.config
        )
        //生成画布
        val canvas = Canvas(bitmap)
        //因为我传入的secondBitmap的大小是不固定的，所以我要将传来的secondBitmap调整到和画布一样的大小
        val w = firstBitmap.width.toFloat()
        val h = firstBitmap.height.toFloat()
        val m = Matrix()
        //确定secondBitmap大小比例
        m.setScale(w / secondBitmap.width, h / secondBitmap.height)
        //给画笔设定透明值，想将哪个图片进行透明化，就将画笔用到那张图片上
        canvas.drawBitmap(firstBitmap, 0f, 0f, null)
        canvas.drawBitmap(secondBitmap, 0f, h, null)
        return bitmap
    }

    /**
     * @param fileName 文件名
     * @param context  上下文
     */
    fun openAssignFolder(fileName: String, context: Context) {
        val file = SDCardUtils.getSDMouthPath(fileName)

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            file
        )
        intent.setDataAndType(uri, "file/*")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    /**
     * 调用系统安装apk
     *
     * @param apkPath 文件路径
     */
    fun installApk(context: Context, apkPath: String) {
        val file = File(apkPath)

        try {
            /**
             * provider
             * 处理android 7.0 及以上系统安装异常问题
             */
            val install = Intent()
            install.action = Intent.ACTION_VIEW
            install.addCategory(Intent.CATEGORY_DEFAULT)
            install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    file
                ) //在AndroidManifest中的android:authorities值
                LogUtils.d("======", "file=${file.path}")
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                install.setDataAndType(
                    Uri.fromFile(file),
                    "application/vnd.android.package-archive"
                )
            }
            context.startActivity(install)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }


    //将下载的文件写入本地存储
    fun writeFile2Disk(
        response: Response<ResponseBody?>,
        file: File,
        downloadListener: DownloadListener
    ) {
//        downloadListener.onStart()
        var currentLength: Long = 0
        var os: OutputStream? = null
        val `is` = response.body()!!.byteStream() //获取下载输入流
        val totalLength = response.body()!!.contentLength()
        try {
            os = FileOutputStream(file) //输出流
            var len: Int
            val buff = ByteArray(1024)
            while (`is`.read(buff).also { len = it } != -1) {
                os.write(buff, 0, len)
                currentLength += len.toLong()
                //计算当前下载百分比，并经由回调传出
//                downloadListener.onProgress((100 * currentLength / totalLength).toInt())
                //当百分比为100时下载结束，调用结束回调，并传出下载后的本地路径
                if ((100 * currentLength / totalLength).toInt() == 100) {
                    downloadListener.onFinish(file.path) //下载完成
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {

            try {
                os?.close() //关闭输出流
                `is`.close() //关闭输入流
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}