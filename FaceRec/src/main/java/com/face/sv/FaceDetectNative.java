package com.face.sv;

/**
 * 人脸识别算法库的人脸检测JNI接口。
 * @author 邹丰
 * @datetime 2016-05-03
 */
public class FaceDetectNative {

    private static FaceDetectNative mNative = null;
    static {
        System.loadLibrary("THFaceImage");
        System.loadLibrary("FaceDetect");
    }

    public static FaceDetectNative getInstance() {
        if (mNative == null) {
            mNative = new FaceDetectNative();
        }
        return mNative;
    }

    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public native byte[] getDetectSN();

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public native int checkDetectSN(byte[] sncode);

    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public native int initFaceDetect(String libDir, String tempDir);

    /**
     * 释放人脸检测算法库
     */
    public native void releaseFaceDetect();

    /**
     * 检测最大一个人脸信息
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran padding 检测边距(默认20)
     * @param angle   偏转角度 人脸可信度(0 ~ 90, 默认20)
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public native byte[] faceDetectMaster(byte[] BGR24, int width, int height, int angle, int padding, float confidence);

    /**
     * 检测最大一个人脸信息
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran params 人脸检测参数{padding.left, padding.top, padding. right, padding.bottom, angle.yaw, angle.pitch, angle.roll}
     *  //人脸检测参数{人脸检测范围.左, 人脸检测范围.上, 人脸检测范围.右, 人脸检测范围.下, 人脸角度.左右角度(0~90), 人脸角度.上下角度(0~90), 人脸角度.旋转(0~90)}
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回检测结果，数组长度4(int) 0表示无人脸，负数表示失败，数组长度580检测到的人脸信息。
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public native byte[] faceDetectMasterExt(byte[] BGR24, int width, int height, int[] params, float confidence);

    /**
     * 检测所有人脸信息(支持返回多人脸信息)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public native byte[] faceDetectAllMaster(byte[] BGR24, int width, int height);

    /**
     * 检测最大一个人脸信息（检测时算法自动缩放到宽度360来进行检测）
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param angle   偏转角度 人脸可信度(0 ~ 90, 默认20)
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @paran padding 检测边距 (人脸识别人脸坐标距离图像边线大于等于padding距离才是有效人脸坐标)
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public native byte[] faceDetectScale(byte[] BGR24, int width, int height, int angle, int padding, float confidence);

    /**
     * 检测最大一个人脸信息(压缩到360)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @paran params 人脸检测参数{padding.left, padding.top, padding. right, padding.bottom, angle.yaw, angle.pitch, angle.roll}
     *  //人脸检测参数{人脸检测范围.左, 人脸检测范围.上, 人脸检测范围.右, 人脸检测范围.下, 人脸角度.左右角度(0~90), 人脸角度.上下角度(0~90), 人脸角度.旋转(0~90)}
     * @param confidence 人脸可信度(0 ~ 1, 默认0.7)
     * @return 返回检测结果，数组长度4(int) 0表示无人脸，负数表示失败，数组长度580检测到的人脸信息。
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public native byte[] faceDetectScaleExt(byte[] BGR24, int width, int height, int[] params, float confidence);

    /**
     * 检测所有人脸信息（检测时算法自动缩放到宽度360来进行检测）
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * 检测结果返回值:
     * 大于0表示检测到人脸；
     * 0 为没有检测到人脸；
     * -99 invalid license；
     * -101 malloc(rgb24)失败；
     * -102 为检测到的人脸边界或角度判断为无效；
     *  -103 为检测到的人脸角度无效；
     */
    public native byte[] faceDetectAllScale(byte[] BGR24, int width, int height);
}
