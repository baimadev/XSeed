package com.holderzone.library

object XSeedClient {

    private val dispatcher = Dispatcher()

    var filePath:String = ""

    fun dispatcher() = dispatcher

    fun init(str:String){
        filePath = str
    }

    fun createRequestAndEnqueue(str: String){
        XSeedRequest
            .Builder {
                content = str
            }
            .build()
            .enqueue()
    }
}