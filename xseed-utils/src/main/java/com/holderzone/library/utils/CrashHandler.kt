package com.holderzone.library.utils

import android.content.Context
import android.util.Log
import com.holderzone.library.XSeedClient
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

class CrashHandler(val context: Context) : Thread.UncaughtExceptionHandler {

    private var mUncaughtHandler: Thread.UncaughtExceptionHandler? = null

    init {
        mUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler() //系统的UncaughtExceptionHandler
    }

    override fun uncaughtException(thread: Thread?, throwable: Throwable?) {

        val eInfo: Writer = StringWriter()
        val printWriter = PrintWriter(eInfo)
        throwable?.printStackTrace(printWriter)

        XSeedClient.executeCrashLog("设备厂商:${DeviceUtils.carrier}")
        XSeedClient.executeCrashLog("设备品牌:${DeviceUtils.brand}")
        XSeedClient.executeCrashLog("设备型号:${DeviceUtils.model}")
        XSeedClient.executeCrashLog("设备号:${DeviceUtils.getDeviceId(context)}")
        XSeedClient.executeCrashLog("崩溃记录：${eInfo.toString()}")
        // 杀死进程
        mUncaughtHandler?.uncaughtException(thread, throwable);

    }
}