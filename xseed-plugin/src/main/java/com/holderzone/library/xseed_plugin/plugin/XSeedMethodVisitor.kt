package com.holderzone.library.xseed_plugin.plugin

import com.holderzone.library.xseed_plugin.utils.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 *
 * @Description:class文件的Method访问器
 * @Author: leisiyu
 * @CreateDate: 2022/1/10
 */
class XSeedMethodVisitor : AdviceAdapter {
    private var config: XSeedConfig? = null

    /**
     * XSeed注解过滤器
     */
    var filter = false

    /**
     * 注解信息临时保存区
     */
    var annotationDesc: String = "方法名：$name"

    constructor(
        api: Int,
        methodVisitor: MethodVisitor,
        access: Int,
        name: String,
        descriptor: String?,
        config: XSeedConfig
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.config = config
    }


    /**
     * 访问方法上的注解
     */
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        api
        if (config?.formatXSeedAnnotation == descriptor) {
            filter = true
            return object : AnnotationVisitor(ASM7) {
                /**
                 * @param name 注解信息名
                 * @param value 注解信息
                 */
                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    annotationDesc += "${name}：${value.toString()}  "
                }
            }
        }
        return super.visitAnnotation(descriptor, visible)
    }


    /**
     * 进入方法回调
     */
    override fun onMethodEnter() {
        super.onMethodEnter()
        Log.log("进入${name}方法")
        //通过注解是否注入这部分代码
        if (filter) {
            mv.visitFieldInsn(
                GETSTATIC,
                "com/holderzone/library/XSeedClient",
                "INSTANCE",
                "Lcom/holderzone/library/XSeedClient;"
            )
            mv.visitLdcInsn(annotationDesc)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/holderzone/library/XSeedClient",
                "createRequestAndEnqueue",
                "(Ljava/lang/String;)V",
                false
            )

        }

    }

    /**
     * 退出方法前回调
     */
    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
    }

}




