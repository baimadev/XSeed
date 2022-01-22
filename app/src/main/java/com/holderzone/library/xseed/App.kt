package com.holderzone.library.xseed

import android.app.Application
import com.holderzone.libs.XSeedClient

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        XSeedClient.init(this)
    }
}