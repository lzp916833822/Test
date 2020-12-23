package com.eloam.process.utils;

import android.util.Base64;
import android.util.Log;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

public class Encrypt3DESAndBase64Utils {

    /**
     *
     * @Description:先3des加密，然后base64编码
     * @param key
     * @param data
     * @return
     */
    public static String encrypt3DESAndBase64(String key, String data){

        byte[] result3Des = encrypt3DES(build3DesKey(key),data,"UTF-8");
        String result = encryptBASE64(result3Des);
        return encodeData(result+",;:"+build3DesKey(key));
    }
    /**
     * 不满足24位的数据补充到24位
     * @param key
     * @return
     */
    private static String build3DesKey(String key) {
        return String.format("%-24s", key).replace(' ', '0');
    }
    /**
     * @Description:使用3DES算法加密
     * @param key 密钥
     * @param data 待加密数据
     *  @param charset 字符编码
     * @return
     */
    public static byte[] encrypt3DES(String key, String data, String charset){

        try {
            byte[] keyByte = key.getBytes(charset);
            byte[] dataByte = data.getBytes(charset);

            //生成密钥
            SecretKey deskey = new SecretKeySpec(keyByte, "DESede");
            //加密
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(dataByte);

        } catch (Exception e) {
            Log.e("zzkong", "encrypt3DES occur error: " + e);
        }
        return null;
    }
    /**
     *
     * @Description:对数据进行base64编码
     * @param data
     * @return
     */
    public static String encryptBASE64(byte[] data) {
        try {
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("zzkong", "encryptBASE64 occur error: " + e);
            return null;
        }
    }
    /**
     *
     * @Description:先base64解码，然后再3des解密
     * @param key
     * @param data
     * @return
     */
    public static String decrypt3DESAndBase64(String key, String data) {
        String result = "";
        if(null != data && !data.isEmpty()) {
            byte[] resultDecryBase64 = decryptBASE64(data);
            result = decrypt3DES(build3DesKey(key),resultDecryBase64,"UTF-8");
        }
        return result;
    }
    /**
     *
     * @Description:使用3DES算法解密
     * @param key 密钥
     * @param data 待解密数据
     * @return
     */
    public static String decrypt3DES(String key, byte[] data, String charset){

        try {
            byte[] keyByte = key.getBytes(charset);
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keyByte, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.DECRYPT_MODE,deskey);
            byte[] resultByte = cipher.doFinal(data);
            return new String(resultByte, charset);
        } catch (Exception e) {
            Log.e("zzkong", "decrypt3DES occur error: " + e);
        }
        return null;
    }
    /**
     -
     - @Description:对数据进行base64解码
     - @param data 待解码数据
     - @return
     */
    public static byte[] decryptBASE64(String data) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            return decoder.decodeBuffer(data);
        } catch (Exception e) {
            Log.e("zzkong", "decryptBASE64 occur error: " + e);
            return null;
        }
    }
    /**
     * 加密字符串
     * @param inputData
     * @return
     */
    public static String encodeData(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return Base64.encodeToString(inputData.getBytes("UTF-8"), Base64.DEFAULT);
        //    return new String(Base64.encodeBase64(inputData.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("zzkong", "encodeData error: " + e);
        }
        return null;
    }
    /**
     * 解密字符串
     * @param inputData
     * @return
     */
    public static String decodeData(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.decode(inputData.getBytes("UTF-8"), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            Log.e("zzkong", "inputData error: " + e);
        }
        return null;
    }

}
