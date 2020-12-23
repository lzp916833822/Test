package com.face.util

import android.content.Context
import android.util.Log
import com.face.sv.FaceDetect
import com.face.sv.FaceLive
import com.face.sv.FaceRecognize
import com.face.sv.SerialDM2016
import com.face.util.FilesUtil.deleteFileByPath
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * @author: lico
 * @Desc:
 */
object FaceRecUtil {
    val TAG = "FaceRecUtil"

    val OLD_FEATURE_DB30A_KO_SO = "libTHFeature_db30a_ko.so"
    val DETECT_KO_SO = "libTHDetect_ko.so"
    val DETECT_KO = "libTHDetect_ko"
    val POS_KO_SO = "libTHFacialPos_ko.so"
    val POS_KO = "libTHFacialPos_ko"
    val FEATURE_DB60A_KO_SO = "libTHFeature_db60a_ko.so"
    val DB60A_KO = "libTHFeature_db60a_ko"
    val LIVE_KO_SO = "libTHFaceLive_ko.so"
    val LIVE_KO = "libTHFaceLive_ko"

    // 默认人脸检测边界像素
    val DEFAULT_DETECT_PADDING = 5
    // 默认人脸检测边界像素
    val DEFAULT_DETECT_ANGLE = 20
    // 默认人脸检测可信度
    val DEFAULT_DETECT_CONFIDENCE = 0.7f
    // 默认活体检测门限
    val DEFAULT_LIVE_LIMIT = 70
    // 默认串口DM2016地址
    val DEFAULT_DM2016_SERIAL = "/dev/ttyS3"

    lateinit var tempDir: String
    lateinit var libDir: String
    lateinit var userDataPath: String

    open fun createFaceDir(context: Context) {
        val dataDir = context.applicationContext.getExternalFilesDir(null)!!.getPath()
        createDir(dataDir)
        libDir = dataDir + "/model/"  // 对应initFaceModel()模型文件创建目录，需要读取权限。
        createDir(libDir)
        tempDir = context.getExternalCacheDir()!!.getPath()
        createDir(tempDir)
        userDataPath = dataDir + "/userData"
        createDir(userDataPath)
    }

    fun initFaceModel(context: Context) {
        val assetMan = context.getResources().getAssets()
        var mainPath: String? = null
        var subPath: String? = null
        var manFile: File? = null
        val subFile: File? = null
        var output: FileOutputStream? = null
        var input: InputStream? = null
        val bts = ByteArray(1024)
        var size = 0
        mainPath = libDir + OLD_FEATURE_DB30A_KO_SO
        manFile = File(mainPath)
        // 如果存在该配置模型文件则删除所有旧模型文件。
        if (manFile.exists()) {
            manFile.delete()

            mainPath = libDir + DETECT_KO_SO
            deleteFileByPath(mainPath)

            mainPath = libDir + POS_KO_SO
            deleteFileByPath(mainPath)

            mainPath = libDir + LIVE_KO_SO
            deleteFileByPath(mainPath)
        }
        // 生成libTHDect_dpbin.so文件
        try {
            mainPath = libDir + DETECT_KO_SO
            manFile = File(mainPath!!)
            if (!manFile.exists()) {
                manFile.createNewFile()
                val dpbins = assetMan.list(DETECT_KO)
                if (dpbins != null && dpbins!!.size > 0) {
                    val len = dpbins!!.size
                    output = FileOutputStream(manFile, true)
                    for (i in 0 until len) {
                        subPath = DETECT_KO + "/" + DETECT_KO + (i + 1)
                        input = assetMan.open(subPath!!)
                        if (input != null) {
                            while (input.read(bts).apply{size = this} != -1) {
                                output.write(bts, 0, size)
                            }
                            input!!.close()
                        } else {
                            log("AssetManager.open(file1) is null. path:" + subPath!!)
                        }
                    }
                    output.flush()
                    output.close()
                } else {
                    log("dpbins == null || dpbins.length <= 0 fileName1:" + DETECT_KO)
                }
            } else {
                log("This model file1 is exist. file:" + mainPath!!)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            log("create model file1 fail. file:" + mainPath!!)
        }
        // 生成libTHFacialPos_con.so文件
        try {
            mainPath = libDir + POS_KO_SO
            manFile = File(mainPath!!)
            if (!manFile.exists()) {
                manFile.createNewFile()
                val cons = assetMan.list(POS_KO)
                if (cons != null && cons!!.size > 0) {
                    val len = cons!!.size
                    output = FileOutputStream(manFile, true)
                    for (i in 0 until len) {
                        subPath = POS_KO + "/" + POS_KO + (i + 1)
                        input = assetMan.open(subPath!!)
                        if (input != null) {
                            while (input.read(bts).apply{size = this} != -1) {
                                output.write(bts, 0, size)
                            }
                            input!!.close()
                        } else {
                            log( "AssetManager.open(file2) is null. path:" + subPath!!)
                        }
                    }
                    output.flush()
                    output.close()
                } else {
                    log( "cons == null || cons.length <= 0 fileName2:" + POS_KO)
                }
            } else {
                log("This model file2 is exist. file:" + mainPath!!)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            log( "create model file3 fail. file:" + mainPath!!)
        }

        // 生成libTHFeature_db60a.so文件
        try {
            mainPath = libDir + FEATURE_DB60A_KO_SO
            manFile = File(mainPath!!)
            if (!manFile.exists()) {
                manFile.createNewFile()
                val db30as = assetMan.list(DB60A_KO)
                if (db30as != null && db30as!!.size > 0) {
                    val len = db30as!!.size
                    output = FileOutputStream(manFile, true)
                    for (i in 0 until len) {
                        subPath = DB60A_KO + "/" + DB60A_KO + (i + 1)
                        input = assetMan.open(subPath!!)
                        if (input != null) {
                            while (input.read(bts).apply{size = this} != -1) {
                                output.write(bts, 0, size)
                            }
                            input!!.close()
                        } else {
                            log("AssetManager.open(file3) is null. path:" + subPath!!)
                        }
                    }
                    output.flush()
                    output.close()

                } else {
                    log("db30as == null || db30as.length <= 0 fileName3:" + DB60A_KO)
                }
            } else {
                log("This model file3 is exist. file:" + mainPath!!)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            log("create model file3 fail. file:" + mainPath!!)
        }

        // 生成libTHFaceLive_vi.so文件
        try {
            mainPath = libDir + LIVE_KO_SO
            manFile = File(mainPath!!)
            if (!manFile.exists()) {
                manFile.createNewFile()
                val vis = assetMan.list(LIVE_KO)
                if (vis != null && vis!!.size > 0) {
                    val len = vis!!.size
                    output = FileOutputStream(manFile, true)
                    for (i in 0 until len) {
                        subPath = LIVE_KO + "/" + LIVE_KO + (i + 1)
                        input = assetMan.open(subPath!!)
                        if (input != null) {
                            while (input.read(bts).apply{size = this} != -1) {
                                output.write(bts, 0, size)
                            }
                            input!!.close()
                        } else {
                            log("AssetManager.open(file4) is null. path:" + subPath!!)
                        }
                    }
                    output.flush()
                    output.close()
                } else {
                    log("vis == null || vis.length <= 0 fileName4:" + LIVE_KO)
                }
            } else {
                log("This model file4 is exist. file:" + mainPath!!)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            log("create model file4 fail. file:" + mainPath!!)
        }
    }
    /**
     * 算法鉴权
     */
    fun authFace(mDM2016: SerialDM2016, mDetect:FaceDetect, mLive:FaceLive, mRecognize:FaceRecognize) : Boolean{
        var status = 0
        var ret = -1
        var output: ByteArray? = null
        var input: ByteArray? = null
        input = mDetect.getDetectSN()
        if (input != null) {
            output = mDM2016.encodeKey(input)
            if (output != null) {
                ret = mDetect.checkDetectSN(output)
                if (ret == 1) {
                    status++
                } else {
                    log("人脸检测算法鉴权失败。ret:$ret")
                }
            }
        }

        input = mLive.getLiveSN()
        if (input != null) {
            output = mDM2016.encodeKey(input)
            if (output != null) {
                ret = mLive.checkLiveSN(output)
                if (ret == 1) {
                    status++
                } else {
                    log("人脸活体检测算法鉴权失败。ret:$ret")
                }
            }
        }

        input = mRecognize.getFeatureSN()
        if (input != null) {
            output = mDM2016.encodeKey(input)
            if (output != null) {
                ret = mRecognize.checkFeatureSN(output)
                if (ret == 1) {
                    status++
                } else {
                    log("人脸识别算法鉴权失败。ret:$ret")
                }
            }
        }
        if (status == 3) {
            return true
        } else {
            return false
        }
    }

    /**
     * 初始化算法库
     */
    fun initAlgorithm(mDetect:FaceDetect, mLive:FaceLive, mRecognize:FaceRecognize) : Boolean{
        var isInitDetect = false
        var isInitLive = false
        var isInitRecognize = false
        var ret = mDetect.initFaceDetect(libDir, tempDir)
      //  log("initFaceDetect() ret:$ret")
        if (ret > 0) {
            isInitDetect = true
        } else {
            isInitDetect = false
        }
        ret = mLive.initFaceLive(libDir, tempDir)
   //     log("initFaceLive() ret:$ret")
        if (ret > 0) {
            isInitLive = true
        } else {
            isInitLive = false
        }
        ret = mRecognize.initFaceRecognize(
            libDir,
            tempDir,
            userDataPath
        )
   //     log("initFaceRecognize() ret:$ret")
        if (ret > 0) {
            isInitRecognize = true
        } else {
            isInitRecognize = false
        }
        if (!isInitDetect || !isInitRecognize) {
            Log.e("zzkong", "人脸检测算法初始化失败");
            return false
        } else {
            Log.i("zzkong", "人脸检测算法初始化成功");
            return true
        }
    }

    private fun log(msg: String) {
        Log.i("zzkong", msg)
    }

    private fun createDir(dir: String) {
        val file = File(dir)
        if (!file.exists() || !file.isDirectory) {
            file.mkdirs()
        }
    }

    private fun createFaceImages(imgName: String, imgPath: String, context: Context) {
        try {
            var img = File(imgPath)
            if (img.exists()) {
                img.delete()
                img = File(imgPath)
            }
            if (!img.exists() || !img.isFile) {
                img.createNewFile()
                val output = FileOutputStream(img)

                val assetMan = context.getResources().getAssets()
                val input = assetMan.open(imgName)
                val bts = ByteArray(1024)
                var size = 0
                size = input.read(bts)
                while (size != -1) {
                    output.write(bts, 0, size)
                }
                output.flush()
                output.close()
                input.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}