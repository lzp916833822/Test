package com.face.sv;

import android.util.Log;

/**
 * 人脸识别算法库的人脸检测。
 * @author 邹丰
 * @datetime 2016-05-03
 */
public class FaceDetect {
    private final static String TAG = "FaceDetect";
    private FaceDetectNative mDetectNative;
    private byte[]  mutexFace = new byte[0];

    public FaceDetect() {
        mDetectNative = FaceDetectNative.getInstance();
        //mDetectNative = new FaceDetectNative();
    }

    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public byte[] getDetectSN() {
        return mDetectNative.getDetectSN();
    }

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public int checkDetectSN(byte[] sncode) {
        return mDetectNative.checkDetectSN(sncode);
    }

    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public int initFaceDetect(String libDir, String tempDir) {
        int ret = mDetectNative.initFaceDetect(libDir, tempDir);
        return ret;
    }

    /**
     * 释放人脸检测算法库
     */
    public void releaseFaceDetect() {
        mDetectNative.releaseFaceDetect();
    }

    /**
     * 检测最大一个人脸信息(检测时算法使用原图进行检测)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran padding 检测边距(默认20)
     * @param angle   偏转角度 人脸可信度(0 ~ 90, 默认20)
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0或小于0，
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public FaceInfo faceDetectMaster(byte[] BGR24, int width, int height, int padding, int angle, float confidence) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
            return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectMaster(BGR24, width, height, padding, angle, confidence);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 检测最大一个人脸信息(检测时算法使用原图进行检测)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran params 人脸检测参数{padding.left, padding.top, padding. right, padding.bottom, angle.yaw, angle.pitch, angle.roll}
     *  //人脸检测参数{人脸检测范围.左, 人脸检测范围.上, 人脸检测范围.右, 人脸检测范围.下, 人脸角度.左右角度(0~90), 人脸角度.上下角度(0~90), 人脸角度.旋转(0~90)}
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0或小于0，
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public FaceInfo faceDetectMasterExt(byte[] BGR24, int width, int height, int[] params, float confidence) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
            return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectMasterExt(BGR24, width, height, params, confidence);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 检测所有人脸信息(支持返回多人脸信息)(检测时算法使用原图进行检测)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0或小于0，
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public FaceInfo faceDetectAllMaster(byte[] BGR24, int width, int height) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
            return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectAllMaster(BGR24, width, height);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 检测最大一个人脸信息（检测时算法自动缩放到宽度360来进行检测）
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran padding 检测边距(默认20)
     * @param angle   偏转角度 人脸可信度(0 ~ 90, 默认20)
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0或小于0，
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public FaceInfo faceDetectScale(byte[] BGR24, int width, int height, int padding, int angle, float confidence) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
            return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectScale(BGR24, width, height, padding, angle, confidence);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 检测最大一个人脸信息(压缩到360)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran params 人脸检测参数{padding.left, padding.top, padding. right, padding.bottom, angle.yaw, angle.pitch, angle.roll}
     *  //人脸检测参数{人脸检测范围.左, 人脸检测范围.上, 人脸检测范围.右, 人脸检测范围.下, 人脸角度.左右角度(0~90), 人脸角度.上下角度(0~90), 人脸角度.旋转(0~90)}
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0或小于0，
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public FaceInfo faceDetectScaleExt(byte[] BGR24, int width, int height, int[] params, float confidence) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
            return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectScaleExt(BGR24, width, height, params, confidence);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 检测所有人脸信息（检测时算法自动缩放到宽度360来进行检测）
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0或小于0，
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public FaceInfo faceDetectAllScale(byte[] BGR24, int width, int height) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
            return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectAllScale(BGR24, width, height);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
