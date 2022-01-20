package com.holderzone.libs

import com.holderzone.libs.utils.XSeedFileUtils
import java.io.InterruptedIOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException

public class XSeedRequest private constructor(val content:String,val filePath:String) : Runnable{

    private constructor(builder: Builder):this(builder.content,builder.filePath)


    class Builder private constructor(){

        constructor(init: Builder.() -> Unit):this(){
            init()
        }

        lateinit var content: String
        lateinit var filePath: String

        fun build() = XSeedRequest(this)
    }

    override fun run() {
        XSeedFileUtils.fileLinesWrite(content,filePath)
    }

    fun enqueue() {
        synchronized(this) {
            XSeedClient.dispatcher().enqueue(this)
        }
    }

     fun executeOn(executorService: ExecutorService) {
        try {
            executorService.execute(this)
        } catch (e: RejectedExecutionException) {
            val ioException = InterruptedIOException("executor rejected")
            ioException.initCause(e)
            println(ioException.message)
        } finally {
            XSeedClient.dispatcher().finished()
        }
    }


}
