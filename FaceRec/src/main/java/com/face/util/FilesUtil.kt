package com.face.util

import java.io.File

/**
 * @author: lico
 * @Desc:
 */
object FilesUtil {

    fun deleteFileByPath(path: String?) {
        if (path == null || path.trim { it <= ' ' }.isEmpty()) {
            return
        }
        val file = File(path)
        if (file.exists() && file.isFile) {
            file.delete()
        }
    }
}