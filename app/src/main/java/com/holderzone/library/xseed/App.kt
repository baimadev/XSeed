package com.holderzone.library.xseed

import android.app.Application
import com.holderzone.library.XseedFileUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        this.externalCacheDir?.let { XseedFileUtils.init(it.path) }
    }
}