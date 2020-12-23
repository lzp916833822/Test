package com.eloam.process.callBack

interface DownloadListener {

    fun onFinish(localPath: String)
    fun onFailure()

}