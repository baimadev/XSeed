package com.holderzone.library.xseed

import android.app.Application
import com.holderzone.library.XSeedClient
import com.holderzone.library.utils.XSeedFileUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
       XSeedClient.init(this)
    }
}