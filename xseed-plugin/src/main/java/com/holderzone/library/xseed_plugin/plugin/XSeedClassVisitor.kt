package com.holderzone.library.xseed_plugin.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @Description:class文件访问器
 * @Author: leisiyu
 * @CreateDate: 2022/1/10
 */
class XSeedClassVisitor : ClassVisitor {
    constructor(classVisitor: ClassVisitor) : super(Opcodes.ASM7,classVisitor)

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return XSeedMethodVisitor(
            api = api,
            methodVisitor = methodVisitor,
            name = name!!,
            access = access,
            descriptor = descriptor,
            config = XSeedConfig()
        )
    }

}