package com.face.cn

import java.nio.ByteBuffer

/**
 * @author: lico
 * @Desc:
 */
class ImageInfo(val width: Int, val height: Int) {
    private var size = 0
    lateinit var data: ByteArray
        private set
    var time: Long = 0
    var isNew: Boolean = false

    init {
        size = width * height * 3 / 2
        data = ByteArray(size)
        isNew = false
    }

    fun setImage(imgData: ByteArray) {
        System.arraycopy(imgData, 0, data!!, 0, imgData.size)
    }

    fun setImage(buffer: ByteBuffer) {
        buffer.get(data!!, 0, buffer.remaining())
    }

    fun loadFromOther(info: ImageInfo?) {
        if (info == null) {
            return
        }
        this.data = info.data
        this.time = info.time
        this.isNew = info.isNew
    }

}
