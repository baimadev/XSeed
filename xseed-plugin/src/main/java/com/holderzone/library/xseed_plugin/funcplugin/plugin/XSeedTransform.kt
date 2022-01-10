package com.holderzone.library.xseed_plugin.funcplugin.plugin

import com.holderzone.library.xseed_plugin.funcplugin.base.BaseTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/7
 */
class XSeedTransform : BaseTransform() {

    override fun modifyClass(byteArray: ByteArray): ByteArray {
        /**
         * 具体对Class文件的操作 具体ASM提供了两种操作Class的方式基于事件方式和基于对象形式
         * 核心Api: 基于事件  使用ClassVisitor操作Class
         * 树Api：基于对象树的形式 使用ClassNode
         */
        val classReader = ClassReader(byteArray)

        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

        val classVisitor = XSeedClassVisitor(classWriter)

        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

}