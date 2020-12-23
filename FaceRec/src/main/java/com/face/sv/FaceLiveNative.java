package com.face.sv;

public class FaceLiveNative {
    private static FaceLiveNative mNative = null;
    static {
        System.loadLibrary("THFaceLive");
        System.loadLibrary("FaceLive");
    }

    public static FaceLiveNative getInstance() {
        if (mNative == null) {
            mNative = new FaceLiveNative();
        }
        return mNative;
    }

    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public native byte[] getLiveSN();

    /**
     * 使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public native int checkLiveSN(byte[] sncode);

    /**
     * 初始化活体检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public native int initFaceLive(String libDir, String tempDir);

    /**
     * 释放活体检测算法库
     */
    public native void releaseFaceLive();

    /**
     * 检测是否活体(可见光彩色图像)
     * @param BGR24KJ 可见光人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posKJ 可见光人脸坐标信息
     * @param nThreshold 活体门限(0~100,默认50)
     * @return 成功返回0（非活体） 或  1（活体）， 失败返回其他。
     * 返回结果：1 表示活体；0 表示非活体； -99 表示鉴权没有成功； -101 表示 malloc(imgKJ)失败； - 102 表示malloc(imgHW)失败；
     */
    public synchronized native int getFaceLiveKj(byte[] BGR24KJ, int width, int height, byte[] posKJ, int nThreshold);

    /**
     * 检测是否活体(红外光黑白图像)
     * @param BGR24HW 红外人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posHW 红外人脸坐标信息
     * @param nThreshold 活体门限(0~100,默认50)
     * @return 成功返回0（非活体） 或  1（活体）， 失败返回其他。
     * 返回结果：1 表示活体；0 表示非活体； -99 表示鉴权没有成功； -101 表示 malloc(imgKJ)失败； - 102 表示malloc(imgHW)失败；
     */
    public synchronized native int getFaceLiveHw(byte[] BGR24HW, int width, int height, byte[] posHW, int nThreshold);

    /**
     * 检测是否活体
     * @param BGR24KJ 可见光人脸图片BGR24数据。
     * @param BGR24HW 红外人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posKJ 可见光人脸坐标信息
     * @param posHW 红外人脸坐标信息
     * @param nThreshold 活体门限(0~100,默认50)
     * @return 成功返回0（非活体） 或  1（活体）， 失败返回其他。
     * 返回结果：1 表示活体；0 表示非活体； -99 表示鉴权没有成功； -101 表示 malloc(imgKJ)失败； - 102 表示malloc(imgHW)失败；
     */
    public synchronized native int getFaceLive(byte[] BGR24KJ, byte[] BGR24HW, int width, int height, byte[] posKJ, byte[] posHW, int nThreshold);
}