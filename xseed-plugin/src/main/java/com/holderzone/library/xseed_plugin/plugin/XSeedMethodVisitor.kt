package com.holderzone.library.xseed_plugin.plugin

import com.holderzone.library.xseed_plugin.plugin.preload.Parameter
import com.holderzone.library.xseed_plugin.plugin.preload.XSeedHookHelper
import com.holderzone.library.xseed_plugin.utils.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import com.holderzone.library.xseed_plugin.utils.OpcodesUtils
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
    var annotationDesc: String = "Method：$name"

    /**
     * method owner
     */
    private var mOwner = ""

    /**
     * 方法参数保存区
     */
    private var mParameters: List<Parameter>? = null

    constructor(
        api: Int,
        methodVisitor: MethodVisitor,
        access: Int,
        name: String,
        descriptor: String?,
        owner: String,
        config: XSeedConfig
    ) : super(api, methodVisitor, access, name, descriptor) {
        this.config = config
        this.mOwner = owner
    }


    /**
     * 访问方法上的注解
     */
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
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
        //通过注解是否注入这部分代码
        if (filter) {
            onHookMethod()
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

    override fun visitEnd() {
        super.visitEnd()
//        XSeedHookHelper.instance?.resetOnMethodEnd()
    }

    /**
     * 入参统计
     */
    private fun onHookMethod() {
        loadParams()
        val printUtilsVarIndex = newLocal(Type.getObjectType("com/holderzone/library/utils/ParameterPrinter"))
        mv.apply {
            visitTypeInsn(Opcodes.NEW, "com/holderzone/library/utils/ParameterPrinter")
            visitInsn(Opcodes.DUP)
            visitLdcInsn(mOwner)
            visitLdcInsn(name)
            visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "com/holderzone/library/utils/ParameterPrinter",
                "<init>",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                false
            )
            visitVarInsn(Opcodes.ASTORE, printUtilsVarIndex)
            Log.log("Parameter List $mParameters")
            mParameters?.forEachIndexed { _, parameter ->
                Log.log("Parameter List $parameter")
                val opcode = OpcodesUtils.getLoadOpcodeFromDesc(parameter.desc)
                val fullyDesc = String.format("(Ljava/lang/String;%s)Lcom/holderzone/library/utils/ParameterPrinter;", parameter.desc)
                visitPrint(printUtilsVarIndex, parameter.index, opcode, parameter.name, fullyDesc)
            }
            visitVarInsn(Opcodes.ALOAD, printUtilsVarIndex)
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/holderzone/library/utils/ParameterPrinter", "print", "()V", false);

        }
    }

    /**
     * access print method parameter
     */
    private fun visitPrint(
        varIndex: Int,
        localIndex: Int,
        opcode: Int,
        name: String,
        desc: String
    ) {
        mv.apply {
            visitVarInsn(Opcodes.ALOAD, varIndex)
            visitLdcInsn(name)
            visitVarInsn(opcode, localIndex)
            visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/holderzone/library/utils/ParameterPrinter",
                "append",
                desc,
                false
            )
            visitInsn(Opcodes.POP)
        }
    }
        /**
         * 加载参数列表
         */
        private fun loadParams() {
            val key: String = name + methodDesc
            Log.log("CollectParamsPlug key:$key")
            mParameters = XSeedHookHelper.instance?.getMethodParams(key)
            if (mParameters != null) {
                Log.log("CollectParamsPlug param lists:$mParameters")
            } else {
                Log.log("CollectParamsPlug param lists null")
            }
        }
    }




