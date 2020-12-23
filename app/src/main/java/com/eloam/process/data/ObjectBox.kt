package com.eloam.process.data

import android.content.Context
import com.eloam.process.BuildConfig
import com.eloam.process.data.entity.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

/**
 * @author: lzp
 * @Desc:
 */
object ObjectBox {
    lateinit var boxStore: BoxStore
    private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()

        if (BuildConfig.DEBUG){
            AndroidObjectBrowser(boxStore).start(context.applicationContext)
        }
    }
}