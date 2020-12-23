package com.eloam.process.utils

import android.text.TextUtils
import android.util.Base64
import java.io.UnsupportedEncodingException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


/**
 * @author: lico
 * @create：2020/6/3
 * @describe：
 */
object EncryptionTool {

    // 定义加密算法，DESede即3DES
    private const val Algorithm = "DESede"
    private const val Key = "E06165932000000000000000"

    /**
     * 3DES加密
     * @param str
     * @return
     */
    fun get3DesString(str: String): String? {
        return if (!TextUtils.isEmpty(str)) {
            val enBytes: ByteArray = encryptMode(Key, str.toByteArray())!!
            Base64.encodeToString(enBytes, Base64.DEFAULT)
        } else {
            null
        }
    }

    /**
     * 加密
     * @param key
     * @param src
     * @return
     */
    private fun encryptMode(key: String, src: ByteArray): ByteArray? {
        return try {
            val deskey: SecretKey = SecretKeySpec(build3DesKey(key), Algorithm)
            val cipher: Cipher = Cipher.getInstance(Algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, deskey)
            cipher.doFinal(src)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 3DES解密
     * @return
     */
    fun getDe3DesString(str: String?): String? {
        return if (!TextUtils.isEmpty(str)) {
            val enBytes =
                Base64.decode(str, Base64.DEFAULT)
            val deBytes: ByteArray = decryptMode(Key, enBytes)!!
            String(deBytes)
        } else {
            null
        }
    }

    /**
     * 解密
     * @param key
     * @param src
     * @return
     */
    private fun decryptMode(key: String, src: ByteArray): ByteArray? {
        return try {
            val deskey: SecretKey = SecretKeySpec(build3DesKey(key), Algorithm)
            val cipher = Cipher.getInstance(Algorithm)
            cipher.init(Cipher.DECRYPT_MODE, deskey)
            cipher.doFinal(src)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据字符串生成密钥24位的字节数组
     * @param keyStr
     * @return
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    private fun build3DesKey(keyStr: String): ByteArray? {
        val key = ByteArray(24)
        val temp = keyStr.toByteArray(charset("UTF-8"))
        if (key.size > temp.size) {
            System.arraycopy(temp, 0, key, 0, temp.size)
        } else {
            System.arraycopy(temp, 0, key, 0, key.size)
        }
        return key
    }
}