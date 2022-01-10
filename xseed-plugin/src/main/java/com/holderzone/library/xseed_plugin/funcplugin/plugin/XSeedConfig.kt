package com.holderzone.library.xseed_plugin.funcplugin.plugin

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/10
 */
/**
 * @param xSeedAnnotation 注解名全路径
 */
class XSeedConfig( private val xSeedAnnotation: String = "com.holderzone.library.annotation.XSeedFuncAnnotation"){

    val formatXSeedAnnotation: String
        get() = "L" + xSeedAnnotation.replace(".", "/") + ";"
}

