package com.holderzone.library

import java.util.*
import java.util.concurrent.*

class Dispatcher {

    val executorService: ExecutorService =
        ThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.SECONDS, LinkedBlockingQueue())

    private val readyXSeedRequest: Queue<XSeedRequest> = LinkedList<XSeedRequest>()

    private var runningXSeedRequest:XSeedRequest? = null

    fun enqueue(XSeedRequest: XSeedRequest) {
        synchronized(this) {
            readyXSeedRequest.add(XSeedRequest)
            promoteAndExecute()
        }

    }

    private fun promoteAndExecute() {
        synchronized (this) {
            if (runningXSeedRequest == null && readyXSeedRequest.size>0){
                runningXSeedRequest = readyXSeedRequest.poll()
                runningXSeedRequest!!.executeOn(executorService)
            }
        }
    }

    fun finished() {
        synchronized(this) {
            runningXSeedRequest = null
            promoteAndExecute()
        }
    }


}