package com.holderzone.library.xseed_plugin.base

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.holderzone.library.xseed_plugin.utils.ClassUtils
import com.holderzone.library.xseed_plugin.utils.DigestUtils
import com.holderzone.library.xseed_plugin.utils.Log
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.Type
import java.io.File
import java.io.FileOutputStream
import java.sql.Time
import java.util.concurrent.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @Author:
 * @Date: 2021/12/8 15:19
 * @Desc:
 */
abstract class BaseTransform : Transform() {

    private val executorService :ThreadPoolExecutor = ThreadPoolExecutor(64,0,0,TimeUnit.SECONDS,LinkedBlockingQueue())
    private val taskList = mutableListOf<Callable<Unit>>()

    override fun transform(transformInvocation: TransformInvocation) {
        Log.log("transform start--------------->")
        onTransformStart()
        val startTime = System.currentTimeMillis()
        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider
        val context = transformInvocation.context
        val isIncremental = transformInvocation.isIncremental
        if (!isIncremental) {
            outputProvider.deleteAll()
        }
        inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                forEachJar(jarInput, outputProvider, context, isIncremental)
            }
            input.directoryInputs.forEach { dirInput ->

                forEachDirectory(dirInput, outputProvider, context, isIncremental)

            }
        }
        val taskListFeature = executorService.invokeAll(taskList)
        taskListFeature.forEach {
            it.get()
        }
        onTransformEnd()
        Log.log("transform end--------------->" + "duration : " + (System.currentTimeMillis() - startTime) + " ms")
    }

    protected open fun onTransformStart() {

    }

    protected open fun onTransformEnd() {

    }

    private fun forEachJar(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider,
        context: Context,
        isIncremental: Boolean
    ) {
        Log.log("jarInput: " + jarInput.file.absolutePath)
        //??? jar ????????? copy ???????????????
        val destFile = outputProvider.getContentLocation(
            DigestUtils.generateJarFileName(jarInput.file),
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        if (isIncremental) {
            when (jarInput.status) {
                Status.NOTCHANGED -> {

                }
                Status.REMOVED -> {
                    Log.log("?????? jar??? " + jarInput.file.absoluteFile + " REMOVED")
                    if (destFile.exists()) {
                        FileUtils.forceDelete(destFile)
                    }
                    return
                }
                Status.ADDED, Status.CHANGED -> {
                    Log.log("?????? jar??? " + jarInput.file.absoluteFile + " ADDED or CHANGED")
                }
                else -> {
                    return
                }
            }
        }
        if (destFile.exists()) {
            FileUtils.forceDelete(destFile)
        }
        val modifiedJar = if (ClassUtils.isLegalJar(jarInput.file)) {
            modifyJar(jarInput.file, context.temporaryDir)
        } else {
            Log.log("???????????? " + jarInput.file.absoluteFile)
            jarInput.file
        }
        FileUtils.copyFile(modifiedJar, destFile)
    }

    private fun modifyJar(jarFile: File, temporaryDir: File): File {
        Log.log("?????? jar??? " + jarFile.absoluteFile)
        val tempOutputJarFile = File(temporaryDir, DigestUtils.generateJarFileName(jarFile))
        if (tempOutputJarFile.exists()) {
            tempOutputJarFile.delete()
        }
        val jarOutputStream = JarOutputStream(FileOutputStream(tempOutputJarFile))
        val inputJarFile = JarFile(jarFile, false)
        try {
            val enumeration = inputJarFile.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val jarEntryName = jarEntry.name
                if (jarEntryName.endsWith(".DSA") || jarEntryName.endsWith(".SF")) {
                    //ignore
                } else {
                    val inputStream = inputJarFile.getInputStream(jarEntry)
                    try {
                        val sourceClassBytes = IOUtils.toByteArray(inputStream)
                        val modifiedClassBytes =
                            if (jarEntry.isDirectory || !ClassUtils.isLegalClass(jarEntryName)) {
                                null
                            } else {
                                modifyJar(sourceClassBytes)
                            }
                        jarOutputStream.putNextEntry(JarEntry(jarEntryName))
                        jarOutputStream.write(modifiedClassBytes ?: sourceClassBytes)
                        jarOutputStream.closeEntry()
                    } finally {
                        IOUtils.closeQuietly(inputStream)
                    }
                }
            }
        } finally {
            jarOutputStream.flush()
            IOUtils.closeQuietly(jarOutputStream)
            IOUtils.closeQuietly(inputJarFile)
        }
        return tempOutputJarFile
    }

    private fun forEachDirectory(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        context: Context,
        isIncremental: Boolean
    ) {
        val dir = directoryInput.file
        //??? Directory ????????? copy ???????????????
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        val srcDirPath = dir.absolutePath
        val destDirPath = dest.absolutePath
        val temporaryDir = context.temporaryDir
        FileUtils.forceMkdir(dest)
        if (isIncremental) {
            val changedFilesMap = directoryInput.changedFiles
            //???????????????class??????
            for (mutableEntry in changedFilesMap) {
                val classFile = mutableEntry.key
                when (mutableEntry.value) {
                    Status.NOTCHANGED -> {
                        continue
                    }
                    Status.REMOVED -> {
                        Log.log("?????? class??? " + classFile.absoluteFile + " REMOVED")
                        //?????????????????????????????????
                        val destFilePath = classFile.absolutePath.replace(srcDirPath, destDirPath)
                        val destFile = File(destFilePath)
                        if (destFile.exists()) {
                            destFile.delete()
                        }
                        continue
                    }
                    Status.ADDED, Status.CHANGED -> {
                        Log.log("?????? class??? " + classFile.absoluteFile + " ADDED or CHANGED")
                        /**
                         * ??????class????????????????????????
                         */
                        submitTask {
                            modifyClassFile(classFile, srcDirPath, dest, dir)
                        }

                    }
                    else -> {
                        continue
                    }
                }
            }
        } else {
            directoryInput.file.walkTopDown().filter { it.isFile }
                .forEach { classFile ->
                    submitTask {
                        modifyClassFile(classFile, srcDirPath, dest, dir)
                    }
                }
        }
    }


    protected fun getVisitPosition(
        argumentTypes: Array<Type>,
        parameterIndex: Int,
        isStaticMethod: Boolean
    ): Int {
        if (parameterIndex < 0 || parameterIndex >= argumentTypes.size) {
            throw Error("getVisitPosition error")
        }
        return if (parameterIndex == 0) {
            if (isStaticMethod) {
                0
            } else {
                1
            }
        } else {
            getVisitPosition(
                argumentTypes,
                parameterIndex - 1,
                isStaticMethod
            ) + argumentTypes[parameterIndex - 1].size
        }
    }

    private fun submitTask(task: () -> Unit) {
        taskList.add(Callable<Unit> {
            task()
        })
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS
        )
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getName(): String {
        return javaClass.simpleName
    }

    override fun isIncremental(): Boolean {
        return true
    }

    /**
     *????????????
     */
    protected abstract fun modifyClassFile(
        classFile: File,
        srcDirPath: String,
        destDirPath: File,
        temporaryDir: File
    )


    protected abstract fun modifyJar(byteArray: ByteArray): ByteArray
}