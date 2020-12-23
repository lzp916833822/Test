package org.lico.core.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException


object BitmapUtil {

    /**
     * String 转 Base64
     */
    fun stringToBase64(string: String) : String{
        return Base64.encodeToString(string.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Base64 转 Bitmap
     */
    fun base64ToBitmap(base64Data: String): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * Bitmap 转 Base64
     */
    @SuppressLint("NewApi")
    fun bitmapToBase64s(bitmap: Bitmap): String? {

        // 要返回的字符串
        var reslut: String? = null

        var baos: ByteArrayOutputStream? = null

        try {

            if (bitmap != null) {

                baos = ByteArrayOutputStream()
                /**
                 * 压缩只对保存有效果bitmap还是原来的大小
                 */
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)

                baos.flush()
                baos.close()
                // 转换为字节数组
                val byteArray = baos.toByteArray()

                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {

            try {
                baos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return reslut

    }

    /**
     * Base64 转 Byte[]
     */
    fun base64String2ByteFun(base64Str: String): ByteArray {
        return Base64.decode(base64Str, Base64.NO_WRAP)
    }

    /**
     * Byte[] 转 Base64
     */
    fun byte2Base64StringFun(b: ByteArray): String {
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    /**
     * Byte[] 转 Bitmap
     */
    fun bytes2Bitmap(b: ByteArray): Bitmap? {
        return if (b.size != 0) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            null
        }
    }

    /**
     * @describe:缩放bitmap
     */
    fun getScaleBitmap(bmp: Bitmap?, scale: Float): Bitmap? {
        if (bmp == null) {
            return null
        }
        val width = bmp.width
        val height = bmp.height
        var tmp: Bitmap? = null
        val matrix = Matrix()
        matrix.postScale(scale, scale) // 长和宽放大缩小的比例
        try {
            tmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false)
        } catch (e: IllegalArgumentException) {
            if (tmp != null && !tmp.isRecycled) {
                tmp.recycle()
            }
            tmp = null
        }

        return tmp
    }
}