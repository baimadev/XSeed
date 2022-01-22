package com.holderzone.library.xseed_plugin.plugin

import com.holderzone.library.xseed_plugin.plugin.preload.XSeedHookHelper
import com.holderzone.library.xseed_plugin.plugin.preload.XSeedPreClassVisitor
import com.holderzone.library.xseed_plugin.utils.Log
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/17
 */
object AppTransformExecutor {

    fun modifyClassPath(dirPath: String){
        if (!dirPath.endsWith(".class")) return
        hookClassPre(dirPath)
        XSeedHookHelper.instance?.mClassList?.forEach { dirPath ->
            hookClass(dirPath)
        }
        XSeedHookHelper.instance?.mClassList?.clear()


    }

    /**
     * 预加载获取实际参数后进行class文件处理
     */
    private fun hookClass(filePath:String) {
        Log.log("Thread---->${Thread.currentThread().name}classFilePath -> $filePath")

        val reader = ClassReader(FileInputStream(filePath))
        val writer = ClassWriter(reader,ClassWriter.COMPUTE_MAXS)
        val classVisitor = XSeedClassVisitor(writer)
        reader.accept(classVisitor,ClassReader.EXPAND_FRAMES)
        val bytes = writer.toByteArray()
        val fos = FileOutputStream(File(filePath))
        fos.write(bytes)
    }

    /**
     * 预加载Class文件获取Method入参
     */
    private fun hookClassPre(filePath:String) {
        Log.logPre("Thread---->${Thread.currentThread().name} classFilePath -> $filePath")
        val reader = ClassReader(FileInputStream(File(filePath)))
        val writer =  ClassWriter(reader, ClassWriter.COMPUTE_MAXS)

        //预处理ClassVisitor
        val preAdapter = XSeedPreClassVisitor(writer,filePath)
        reader.accept(preAdapter, ClassReader.EXPAND_FRAMES)
    }
}