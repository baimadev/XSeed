package com.holderzone.library.xseed_plugin.utils

import java.util.concurrent.Executors

/**
 * @Author: leavesC
 * @Date: 2021/12/8 10:57
 * @Desc:
 */
object Log {

    private val logThreadExecutor = Executors.newSingleThreadExecutor()

    fun log(log: Any?) {
        logThreadExecutor.submit {
            println("ASM: $log")
        }
    }
    fun logAnnotation(log: Any?){
        logThreadExecutor.submit{
            println("[ Annotaiton ] :$log")
        }
    }

}