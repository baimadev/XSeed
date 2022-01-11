package com.holderzone.library

import com.holderzone.library.utils.XSeedFileUtils
import java.io.InterruptedIOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException

public class XSeedRequest private constructor(val content:String) : Runnable{

    private constructor(builder:Builder):this(builder.content)


    class Builder private constructor(){

        constructor(init:Builder.() -> Unit):this(){
            init()
        }

        lateinit var content: String

        fun setContent(init: Builder.() -> String) = apply { content = init() }
        fun build() = XSeedRequest(this)
    }

    override fun run() {
        XSeedFileUtils.fileLinesWrite(content)
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
