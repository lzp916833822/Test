package com.face.sv;

/**
 * 人脸识别算法库的人脸检测JNI接口。
 * @author zoufeng
 * @datetime 2018-01-30
 */
public class FaceRecognizeNative {
    private static FaceRecognizeNative mNative = null;
    static {
        System.loadLibrary("JPG");
        System.loadLibrary("THFeature");
        System.loadLibrary("FaceRecognize");
    }

    public static FaceRecognizeNative getInstance() {
        if (mNative == null) {
            mNative = new FaceRecognizeNative();
        }
        return mNative;
    }

    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public native byte[] getFeatureSN();

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public native int checkFeatureSN(byte[] sncode);

    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @ userDataPath 用户模型数据存储路径
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public native int initFaceRecognize(String libDir, String tempDir, String userDataPath);

    /**
     * 释放人脸检测算法库
     */
    public native void releaseFaceRecognize();


    /**
     * 加载人脸模型
     * @return 0为成功，小于0 表示失败。
     */
    public native int loadAllFaceFeature();

    /**
     * 获取人脸模板
     * @param BGR24 人脸图片BGR24
     * @param width 图片宽度
     * @param height 图片高度
     * @param facePos  图片人脸信息
     * @return 返回结果，数组长度4(int)负数表示失败，数组长度2560获取到的模型信息。
     * 检测结果返回值: -99 invalid license；-101 malloc(rgb24)失败； -102 人脸信息不全； -103 malloc(pFeature)失败;
     */
    public native byte[] getFaceFeature(byte[] BGR24, int width, int height, byte[] faceInfo);

    /**
     * 比对两个人脸模板相似度
     * @param feature1 人脸模板
     * @param feature2 人脸模板
     * @return 返回结果，相识度（分值范围0 ~ 100之间）, 负数表示失败。
     * 检测结果返回值: -99 invalid license；-101 malloc(feaOne)失败； -102 malloc(feaTwo)失败；
     */
    public native int compareFeature(byte[] feature1, byte[] feature2);

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
     * -101表示没有找到用户模型数据;-102 改用户内存模型指针为空;）
     */
    public native byte[] getMemoryFaceFeature(int userId);

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为4的整形错误码。
     * (错误码：-101表示申请内存失败;-102 加载模型文件失败;）
     */
    public native byte[] getFileFaceFeature(int userId);

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0注册成功，表示注册模型编号,小于0表示失败。
     *  * error code:
     * -1,pBuf,ptfp,pFeature is NULL
     * -2,nChannelID is invalid or SDK is not initialized
     * -99,invalid license.
     * -101 malloc(rgb24)失败.
     * -102 facePos数据错误.
     * -103 malloc(pFeature) fail.
     * -104 getFaceFeature() fail.
     * -105 saveUserFeatureToFile() fail.
     * -106 getEmptyUserInfoToRegister() is NULL.
     */
    public native int registerFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * 删除人脸模型
     * @ userId 注册用户模型编号
     * @return 0为成功，小于0 表示失败
     */
    public native int deleteFaceFeature(int userId);

    /**
     * 清空所有人脸模型
     * @return 0为成功，小于0 表示失败
     */
    public native int clearAllFaceFeature();

    /**
     * 更新人脸模型
     * @ faceId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0更新成功，表示更新模型编号（0-N),小于0表示失败。
     *  * error code:
    -1,pBuf,ptfp,pFeature is NULL
    -2,nChannelID is invalid or SDK is not initialized
    -99,invalid license.
    -101 malloc(rgb24) 失败 或 BGR24数据出错.
    -102 人脸信息facePos数据错误。
    -103 malloc(pFeature)失败。
    -104 获取人脸模型失败.
    -105 保存模型信息到文件失败.
    -106 获取空的模型对象失败.
     */
    public native int updateFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * 1对1人脸模型识别
     * @ userId 比对用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0识别成功，返回识别相似度（0-100),小于0表示失败。
     * error code:
    -99,invalid license.
    -101 malloc(rgb24) 失败 或 BGR24数据出错.
    -102 人脸信息数据错误。。
    -103 获取人脸模型失败.
    -104 根据工号获取用户模型对象失败.
     */
    public native int recognizeFaceOne(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * 1对多人脸模型识别
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸信息
     * @return int[0]大于等于0注册成功,返回用户编号,小于0表示失败。int[1]成功时表示识别分数
     * error code:
    -99,invalid license.
    -101 malloc(rgb24) 失败 或 BGR24数据出错.
    -102 人脸信息facePos数据错误。
    -103 获取人脸模型失败.
     */
    public native int[] recognizeFaceMore(byte[] BGR24, int width, int height, byte[] facePos);
}
