package com.holderzone.library.annotation

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/10
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class XSeedFuncAnnotation(val functionDesc:String = "XXX用户登录",val tag:String = "login",val isSeedParam:Boolean = true)
