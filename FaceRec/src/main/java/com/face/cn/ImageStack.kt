package com.face.cn

import android.util.Log
import java.nio.ByteBuffer

/**
 * @author: lico
 * @Desc:
 */
class ImageStack(width: Int, height: Int) {
    private var imageOne: ImageInfo? = null
    lateinit var imageTwo: ImageInfo
    val mLock = Any()
    init {
        imageOne = ImageInfo(width, height)
        imageTwo = ImageInfo(width, height)
    }

    fun pullImageInfo(): ImageInfo {
        synchronized(mLock) {
            if (imageOne!!.isNew) {
                imageTwo!!.setImage(imageOne!!.data)
                imageTwo!!.time = imageOne!!.time
                imageTwo!!.isNew = true
                imageOne!!.isNew = false
            } else {
                imageTwo!!.isNew = false
            }
            return imageTwo
        }
    }

    fun pushImageInfo(imgData: ByteArray, time: Long) {
        //log("pushImageInfo(byte[]) isReading:" + isReading);
        synchronized(mLock) {
            imageOne!!.setImage(imgData)
            imageOne!!.time = time
            imageOne!!.isNew = true
        }
    }

    fun pushImageInfo(buffer: ByteBuffer, time: Long) {
        //log("pushImageInfo(ByteBuffer) isReading:" + isReading);
        synchronized(mLock) {
            imageOne!!.setImage(buffer)
            imageOne!!.time=time
            imageOne!!.isNew = true
            //log("pullImageInfo() imageOne.setNew(true)");
        }
    }

    fun clearAll() {
        synchronized(mLock) {
            imageOne!!.isNew=false
            imageTwo!!.isNew=false
        }
    }

    companion object {
        private val TAG = ImageStack::class.java.simpleName

        fun log(msg: String) {
            Log.e(TAG, msg)
        }
    }
}
