package com.holderzone.library.xseed_plugin.funcplugin.plugin

import com.holderzone.library.xseed_plugin.funcplugin.utils.Log
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

    var filter = false

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
        if ( config?.formatXSeedAnnotation == descriptor ){
            filter = true
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
//            mv.visitFieldInsn(
//                GETSTATIC,
//                "com/holderzone/library/XseedFileUtils",
//                "INSTANCE",
//                "Lcom/holderzone/library/XseedFileUtils;"
//            )
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitLdcInsn("CLICK")
//            mv.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "com/holderzone/library/XseedFileUtils",
//                "fileLinesWrite",
//                "(Ljava/lang/String;)V",
//                false
//            );


            mv.visitLdcInsn("TAG")
            mv.visitLdcInsn("CLICK")
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/util/Log",
                "e",
                "(Ljava/lang/String;Ljava/lang/String;)I",
                false
            )
            mv.visitInsn(Opcodes.POP)
        }

    }

    /**
     * 退出方法前回调
     */
    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
    }

}