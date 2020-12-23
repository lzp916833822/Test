package org.lico.core.base

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin

/**
 * @author: lico
 * @Desc:
 */
open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}