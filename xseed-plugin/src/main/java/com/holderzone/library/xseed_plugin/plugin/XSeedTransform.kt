package com.holderzone.library.xseed_plugin.plugin

import com.holderzone.library.xseed_plugin.base.BaseTransform
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File

/**
 *
 * @Description:
 * 具体对Class文件的操作 具体ASM提供了两种操作Class的方式基于事件方式和基于对象形式
 * 核心Api: 基于事件  使用ClassVisitor操作Class
 * 树Api：基于对象树的形式 使用ClassNode
 * @Author: leisiyu
 * @CreateDate: 2022/1/7
 */
class XSeedTransform : BaseTransform() {

    override fun modifyJar(byteArray: ByteArray): ByteArray {

        val classReader = ClassReader(byteArray)

        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

        val classVisitor = XSeedClassVisitor(classWriter)

        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }


    override fun modifyClassFile(
        classFile: File,
        srcDirPath: String,
        destDirPath: File,
        temporaryDir: File
    ) {
        //将此方法抽解出来做Class文件做预加载处理
        AppTransformExecutor.modifyClassPath(classFile.absolutePath)
        // 将修改过的字节码copy到dest，实现编译期间干预字节码
        try {
            FileUtils.copyDirectory(temporaryDir, destDirPath)
        } catch (e: Exception) {
            print(e)
        }
    }
}