package com.holderzone.library.xseed_plugin.plugin

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/10
 */
/**
 * @param xSeedAnnotation 注解名全路径
 */
class XSeedConfig(
    private val xSeedAnnotation: String = "com.holderzone.libs.annotation.XSeedFuncAnnotation",
    private val xSeedClassAnnotation: String = "com.holderzone.libs.annotation.XSeedClassAnnotation"
){

    val formatXSeedAnnotation: String
        get() = "L" + xSeedAnnotation.replace(".", "/") + ";"
    val formatXSeedClassAnnotation: String
        get() = "L" + xSeedClassAnnotation.replace(".", "/") + ";"
}

