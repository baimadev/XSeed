package com.holderzone.library.xseed_plugin.plugin.preload

import com.holderzone.library.xseed_plugin.plugin.XSeedConfig
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import java.util.*

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/17
 */
class XSeedPreMethodVisitor : AdviceAdapter {

    /**
     * 注解过滤器
     */
    private var filter:Boolean = false

    private var config: XSeedConfig? = null

    private var parameters: MutableList<Parameter>? = null
    private var labelList: MutableList<Label>? = null
    /**
     * 存储方法参数的key,方法名+返回值
     */
    private var mMethodKey: String = ""
    private var mMethodName: String = ""
    private var mOwner: String = ""

    constructor(
        api: Int,
        methodVisitor: MethodVisitor,
        access: Int,
        name: String,
        descriptor: String?,
        owner: String,
        config: XSeedConfig
    ) : super(api, methodVisitor, access, name, descriptor) {
        mMethodKey = name + descriptor
        print(mMethodKey)
        mMethodName = name
        mOwner = owner
        this.config = config
    }



    /**
     * 访问方法上的注解
     */
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (config?.formatXSeedAnnotation == descriptor) {
            filter = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitCode() {
        super.visitCode()
        parameters = mutableListOf()
        labelList = mutableListOf()
    }

    /**
     * 方法访问完毕
     */
    override fun visitEnd() {
        super.visitEnd()
        //将获取到的参数list保存到helper类
        parameters?.let { parameters ->
            if(parameters.size > 0){
                XSeedHookHelper.instance?.putMethodParams(mMethodKey, parameters)
            }
        }

    }

    /**
     * 预处理方法入参
     */
    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)

        if(filter){
            if("this" != name  && start == labelList?.get(0)){
                val type: Type = Type.getType(descriptor)
                if (type.sort == Type.OBJECT || type.sort == Type.ARRAY) {
                    parameters?.add(Parameter(name!!, "Ljava/lang/Object;", index))
                } else {
                    parameters?.add(Parameter(name!!, descriptor!!, index))
                }
            }
        }
    }


    override fun visitLabel(label: Label?) {
        if (label != null) {
            labelList?.add(label)
        }
        super.visitLabel(label)
    }
}