package com.holderzone.library.xseed_plugin.plugin

import com.holderzone.library.xseed_plugin.plugin.preload.Parameter
import com.holderzone.library.xseed_plugin.plugin.preload.XSeedHookHelper
import com.holderzone.library.xseed_plugin.utils.Log
import com.holderzone.library.xseed_plugin.utils.OpcodesUtils
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
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
    private var filter = false

    /**
     * 注解信息临时保存区
     */
    var annotationDesc: String = "Method：$name {"

    /**
     * method owner
     */
    private var mOwner = ""

    /**
     * 方法参数保存区
     */
    private var mParameters: List<Parameter> = mutableListOf()

    /**
     * isSeedParam 是否需要打印入参列表
     */
    private var isSeedParam: Boolean = false


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

                    if (name != "isSeedParam") {
                        annotationDesc += "${name}：${value.toString()} "
                    }else{
                        isSeedParam = value as Boolean
                    }
                }

                override fun visitEnd() {
                    super.visitEnd()
                    annotationDesc += "}\n"
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
            if(isSeedParam){
                onHookMethodParam()
            }else{
                visitFieldInsn(
                    GETSTATIC,
                    "com/holderzone/library/XSeedClient",
                    "INSTANCE",
                    "Lcom/holderzone/library/XSeedClient;"
                )
                mv.visitLdcInsn(annotationDesc)
                visitMethodInsn(
                    INVOKEVIRTUAL,
                    "com/holderzone/library/XSeedClient",
                    "executeMethodLog",
                    "(Ljava/lang/String;)V",
                    false
                )
            }
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
    }

    /**
     * 入参统计 com/holderzone/library/utils/ParameterPrinter
     */
    private fun onHookMethodParam() {
        //加载参数列表
        loadParams()
        val printUtilsVarIndex =
            newLocal(Type.getObjectType("com/holderzone/library/utils/ParameterPrinter"))
        println("printUtilsVarIndex---$printUtilsVarIndex parametersize---${mParameters.size + 1}")
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
            mParameters?.forEachIndexed { _, parameter ->
                Log.log("Parameter List $parameter")
                val opcode = OpcodesUtils.getLoadOpcodeFromDesc(parameter.desc)
                val fullyDesc = String.format(
                    "(Ljava/lang/String;%s)Lcom/holderzone/library/utils/ParameterPrinter;",
                    parameter.desc
                )
                visitPrint(printUtilsVarIndex, parameter.index, opcode, parameter.name, fullyDesc)
            }
            visitFieldInsn(
                GETSTATIC,
                "com/holderzone/library/XSeedClient",
                "INSTANCE",
                "Lcom/holderzone/library/XSeedClient;"
            )
            visitTypeInsn(NEW, "java/lang/StringBuilder")
            visitInsn(DUP)
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)

            visitLdcInsn(annotationDesc)
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )
            visitVarInsn(Opcodes.ALOAD, printUtilsVarIndex)
            visitMethodInsn(
                INVOKEVIRTUAL,
                "com/holderzone/library/utils/ParameterPrinter",
                "print",
                "()Ljava/lang/String;",
                false
            )
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;",
                false
            )
            visitMethodInsn(
                INVOKEVIRTUAL,
                "com/holderzone/library/XSeedClient",
                "executeMethodLog",
                "(Ljava/lang/String;)V",
                false
            )

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
        mParameters = XSeedHookHelper.instance?.getMethodParams(key)!!
        if (mParameters != null) {
            Log.log("CollectParamsPlug param lists:$mParameters")
        } else {
            Log.log("CollectParamsPlug param lists null")
        }
    }
}




