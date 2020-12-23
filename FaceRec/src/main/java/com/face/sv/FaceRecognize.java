package com.face.sv;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 人脸识别算法库的人脸检测。
 * @author 邹丰
 * @datetime 2018-02-05
 */
public class FaceRecognize {
    private final static String TAG = "FaceRecognize";
    private FaceRecognizeNative mRecognizeNative;
    private byte[] mutexFeature = new byte[0];
    private byte[] mutexCompare = new byte[0];

    public FaceRecognize() {
        mRecognizeNative = FaceRecognizeNative.getInstance();
    }

    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public byte[] getFeatureSN() {
        return mRecognizeNative.getFeatureSN();
    }

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public int checkFeatureSN(byte[] sncode) {
        return mRecognizeNative.checkFeatureSN(sncode);
    }

    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public int initFaceRecognize(String libDir, String tempDir, String userDataPath) {
        return mRecognizeNative.initFaceRecognize(libDir, tempDir, userDataPath);
    }

    /**
     * 释放人脸检测算法库
     */
    public void releaseFaceRecognize() {
        mRecognizeNative.releaseFaceRecognize();
    }

    /**
     * 加载人脸模型
     * @return 0为成功，小于0 表示失败。
     */
    public int loadAllFaceFeature() {
        return mRecognizeNative.loadAllFaceFeature();
    }

    /**
     * 获取人脸图片中人脸模板
     * @param BGR24 人脸图片
     * @param width 图片宽度
     * @param height 图片高度
     * @param facePos  图片人脸坐标信息(FacePos.data), 数组长度580(有多个人脸则 截取需要检测的人脸信息)
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
     * (-101表示传入数据为空;-102表示malloc模型失败;103表示传入的faceInfo信息错误;
     */
    public byte[] getFaceFeatureFromRGB(byte[] BGR24, int width, int height, byte[] facePos) {
        byte[] feature = null;
        if (facePos != null && facePos.length == FacePos.SIZE) {
            // 获取人脸模板
            synchronized (mutexFeature) {
                feature = mRecognizeNative.getFaceFeature(BGR24, width, height, facePos);
            }
        }
        return feature;
    }

    /**
     * 获取人脸图片中人脸模板
     * @param bmp 人脸图片
     * @param facePos  图片人脸坐标信息(FacePos.data), 数组长度580(有多个人脸则 截取需要检测的人脸信息)
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
     * (-101表示传入数据为空;-102表示malloc模型失败;103表示传入的faceInfo信息错误;
     */
    public byte[] getFaceFeatureFromBitmap(Bitmap bmp, byte[] facePos) {
        log("getFaceFeatureFromBitmap(Bitmap bmp)");
        final int width = bmp.getWidth();
        final int height = bmp.getHeight();
        byte[] feature = null;
        if (facePos != null && facePos.length == FacePos.SIZE && mRecognizeNative != null) {
            int size = width * height;
            int[] pixels = new int[size];
            // 获取RGB32数据
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] BGR24 = new byte[size * 3];
            //byte[] gray = new byte[width * height];
            // 获取图片的RGB24数据和灰度图数据
            int item = 0;
            for (int i = 0; i < size; i++) {
                item = pixels[i];
                //int r = (value >> 16) & 0x000000FF;
                //int g = (value >> 8) & 0x000000FF;
                //int b = value & 0x000000FF;
                BGR24[i * 3] = (byte)(item & 0xFF);
                BGR24[i * 3 + 1] = (byte)((item >> 8) & 0xFF);
                BGR24[i * 3 + 2] = (byte)((item >> 16) & 0xFF);
                //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
            }

            // 获取人脸模板
            synchronized (mutexFeature) {
                feature = mRecognizeNative.getFaceFeature(BGR24, width, height, facePos);
            }
        }
        return feature;
    }

    /**
     * 比对两个人脸模板相似度
     * @param bmp1 人脸图片1
     * @param bmp2 人脸图片2
     * @param faceInfo1  图片人脸坐标信息(FacePos.data), 数组长度580(有多个人脸则 截取需要检测的人脸信息)
     * @param faceInfo2  图片人脸坐标信息(FacePos.data), 数组长度580(有多个人脸则 截取需要检测的人脸信息)
     * @return 相识度（分值范围0 ~ 100之间）
     */
    public int compareFaces(Bitmap bmp1, byte[] faceData1, Bitmap bmp2, byte[] faceData2) {
        log("compareFaces(Bitmap bmp1, Bitmap bmp2)");
        int ret = -1;
        byte[] feature1 = null;
        byte[] feature2 = null;
        // 获取模板
        feature1 = getFaceFeatureFromBitmap(bmp1, faceData1);
        // 获取模板成功
        if (feature1 != null && feature1.length > 1) {
            // 获取模板
            feature2 = getFaceFeatureFromBitmap(bmp2, faceData2);
            if (feature2 != null && feature2.length > 1) {
                // 比对两个模板相似度
                synchronized (mutexCompare) {
                    ret = mRecognizeNative.compareFeature(feature1, feature2);
                }
            } else {
                log("feature2 == null && feature2.length <= 1");
            }
        } else {
            log("feature1 == null && feature1.length <= 1");
        }
        return ret;
    }

    /**
     * 比对两个人脸模板相似度
     * @param feature1 人脸模板
     * @param feature2 人脸模板
     * @return 相识度（分值范围0 ~ 100之间）， -1 表示参数feature1或feature2为null.
     */
    public int compareFeatures(final byte[] feature1, final byte[] feature2) {
        log("compareFeatures(final float[] feature1:"+feature1+", final float[] feature2:)"+feature2);
        int ret = -1;
        if (mRecognizeNative != null) {
            if (feature1 != null && feature1.length > 1) {
                if (feature2 != null && feature2.length > 1) {
                    // 比对两个模板相似度
             //       synchronized (mutexCompare) {
                        try {
                            ret = mRecognizeNative.compareFeature(feature1, feature2);
                        }catch (Exception e){
                            Log.e("zzkong", "错误:" + e.toString());
                            e.printStackTrace();
                        }
             //       }
                } else {
                    log("feature2 == null && feature2.length <= 1");
                }
            } else {
                log("feature1 == null && feature1.length <= 1");
            }
        }
        return ret;
    }

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
     * -101表示没有找到用户模型数据;-102 改用户内存模型指针为空;）
     */
    public byte[] getMemoryFaceFeature(int userId) {
        byte[] bts = null;
        if (mRecognizeNative != null) {
            bts = mRecognizeNative.getMemoryFaceFeature(userId);
        }
        return bts;
    }

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为4的整形错误码。
     * (错误码：-101表示申请内存失败;-102 加载模型文件失败;）
     */
    public byte[] getFileFaceFeature(int userId) {
        byte[] bts = null;
        if (mRecognizeNative != null) {
            bts = mRecognizeNative.getFileFaceFeature(userId);
        }
        return bts;
    }

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0注册成功，表示注册模型编号（0-N),小于0表示失败。
     *  * error code:
    -1,pBuf,ptfp,pFeature is NULL
    -2,nChannelID is invalid or SDK is not initialized
    -99,invalid license.
    -100 pFacePos is NULL or size too smaller.
    -101 faceFeature malloc fail.
    -102 save UserFeature data fail.
    -103 get empty UserInfo object fail.
     */
    public int registerFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos) {
        return mRecognizeNative.registerFaceFeature(userId, BGR24, width, height, facePos);
    }

    /**
     * 删除人脸模型
     * @ userId 注册用户模型编号
     * @return 0为成功，小于0 表示失败
     */
    public int deleteFaceFeature(int userId) {
        return mRecognizeNative.deleteFaceFeature(userId);
    }

    /**
     * 清空所有人脸模型
     * @return 0为成功，小于0 表示失败
     */
    public int clearAllFaceFeature() {
        return mRecognizeNative.clearAllFaceFeature();
    }

    /**
     * 注册人脸模型
     * @ faceId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0更新成功，表示更新模型编号（0-N),小于0表示失败。
     * error code:
    -1,pBuf,ptfp,pFeature is NULL
    -2,nChannelID is invalid or SDK is not initialized
    -99,invalid license.
    -100 pFacePos is NULL or size too smaller.
    -101 faceFeature malloc fail.
    -102 save UserFeature data fail.
    -103 get UserInfo object fail.
     */
    public int updateFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos) {
        return mRecognizeNative.updateFaceFeature(userId, BGR24, width, height, facePos);
    }

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0识别成功，返回识别相似度（0-100),小于0表示失败。
     */
    public int recognizeFaceOne(int userId, byte[] BGR24, int width, int height, byte[] facePos) {
        return mRecognizeNative.recognizeFaceOne(userId, BGR24, width, height, facePos);
    }

    /**
     * 注册人脸模型
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return int[0]大于等于0注册成功,返回用户编号,小于0表示失败。int[1]成功时表示识别分数
     */
    public int[] recognizeFaceMore(byte[] BGR24, int width, int height, byte[] facePos) {
        return mRecognizeNative.recognizeFaceMore(BGR24, width, height, facePos);
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
