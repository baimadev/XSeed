package com.holderzone.libs

import android.content.Context
import com.holderzone.libs.utils.CrashHandler

object XSeedClient {

    private val dispatcher = Dispatcher()

    var filePath:String = ""

    fun dispatcher() = dispatcher

    fun init(context: Context){
        //设置路径
        context.externalCacheDir?.let { filePath = it.path }
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(context))
    }

    fun executeMethodLog(str: String){
        XSeedRequest.Builder {
            content = str
            filePath = "${XSeedClient.filePath}/method_log.txt"
        }
            .build()
            .enqueue()
    }

    fun executeCrashLog(str: String){
        XSeedRequest.Builder {
            content = str
            filePath = "${XSeedClient.filePath}/crash_log.txt"
        }
            .build()
            .enqueue()
    }
}