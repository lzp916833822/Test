package com.face.sv;

public class FaceLive {
    private FaceLiveNative mLiveNative;
    private byte[]  mutexFace = new byte[0];

    public FaceLive() {
        mLiveNative = FaceLiveNative.getInstance();
        //mLiveNative = new FaceLiveNative();
    }

    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public byte[] getLiveSN() {
        return mLiveNative.getLiveSN();
    }

    /**
     * 使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public int checkLiveSN(byte[] sncode) {
        return mLiveNative.checkLiveSN(sncode);
    }

    /**
     * 初始化活体检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public int initFaceLive(String libDir, String tempDir) {
        return mLiveNative.initFaceLive(libDir, tempDir);
    }

    /**
     * 释放活体检测算法库
     */
    public void releaseFaceLive() {
        mLiveNative.releaseFaceLive();
    }

    /**
     * 检测是否活体(可见光彩色图像)
     * @param BGR24KJ 可见光人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posKJ 可见光人脸坐标信息
     * @param nThreshold 活体门限(0~100,默认50)
     * @return 返回0（非活体） 或  1（活体）， 失败返回其他。
     */
    public int getFaceLiveKj(byte[] BGR24KJ, int width, int height, byte[] posKJ, int nThreshold) {
        int ret = -1;
        synchronized (mutexFace) {
            ret = mLiveNative.getFaceLiveKj(BGR24KJ, width, height, posKJ, nThreshold);
        }
        return ret;
    }

    /**
     * 检测是否活体(红外光黑白图像)
     * @param BGR24HW 红外人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posHW 红外人脸坐标信息
     * @param nThreshold 活体门限(0~100,默认50)
     * @return 返回0（非活体） 或  1（活体）， 失败返回其他。
     */
    public int getFaceLiveHw(byte[] BGR24HW, int width, int height, byte[] posHW, int nThreshold) {
        int ret = -1;
        synchronized (mutexFace) {
            ret = mLiveNative.getFaceLiveHw(BGR24HW, width, height, posHW, nThreshold);
        }
        return ret;
    }

    /**
     * 检测是否活体
     * @param BGR24KJ 可见光人脸图片BGR24数据。
     * @param BGR24HW 红外人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posKJ 可见光人脸坐标信息
     * @param posHW 红外人脸坐标信息
     * @param nThreshold 活体门限(0~100,默认50)
     * @return 返回0（非活体） 或  1（活体）， 失败返回其他。
     */
    public int getFaceLive(byte[] BGR24KJ, byte[] BGR24HW, int width, int height, byte[] posKJ, byte[] posHW, int nThreshold) {
        int ret = -1;
        synchronized (mutexFace) {
            ret = mLiveNative.getFaceLive(BGR24KJ, BGR24HW, width, height, posKJ, posHW, nThreshold);
        }
        return ret;
    }
}