package com.holderzone.library.xseed_plugin.plugin.preload

import com.holderzone.library.xseed_plugin.plugin.XSeedConfig
import com.holderzone.library.xseed_plugin.plugin.XSeedMethodVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.awt.datatransfer.ClipboardOwner

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/17
 */
class XSeedPreClassVisitor : ClassVisitor {
    private lateinit var owner: String

    constructor(classVisitor: ClassVisitor) : super(Opcodes.ASM7,classVisitor)

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        if (name != null) {
            this.owner = name
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return XSeedPreMethodVisitor(
            api = api,
            methodVisitor = methodVisitor,
            name = name!!,
            access = access,
            descriptor = descriptor,
            owner = owner,
            config = XSeedConfig()
        )
    }
}