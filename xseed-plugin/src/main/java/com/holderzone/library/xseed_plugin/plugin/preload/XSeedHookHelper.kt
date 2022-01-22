package com.holderzone.library.xseed_plugin.plugin.preload

import com.holderzone.library.xseed_plugin.utils.Log
import java.util.*

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/13
 */
class XSeedHookHelper private constructor() {

    // 方法参数map
    private val mMethodParametersMap: MutableMap<String, List<Parameter>>

    //预加载需要加载的ClassPath
    val mClassList:MutableList<String> = mutableListOf()

    fun putMethodParams(key: String, value: List<Parameter>) {
        mMethodParametersMap[key] = value
        Log.logPre("参数列表:$mMethodParametersMap")
    }

    fun getMethodParamsMap(): Map<String, List<Parameter>> {
        return mMethodParametersMap
    }

    fun getMethodParams(key: String): List<Parameter>? {
        return if (mMethodParametersMap.containsKey(key)) {
            mMethodParametersMap[key]
        } else null
    }

    // 清空数据
    fun resetOnMethodEnd() {
        //清空方法参数
        mMethodParametersMap.clear()
    }

    companion object {
        private var mInstance: XSeedHookHelper? = null
        val instance: XSeedHookHelper?
            get() {
                if (mInstance == null) {
                    synchronized(XSeedHookHelper::class.java) {
                        if (mInstance == null) {
                            mInstance = XSeedHookHelper()
                        }
                    }
                }
                return mInstance
            }
    }

    init {
        mMethodParametersMap = HashMap<String, List<Parameter>>()
    }
}