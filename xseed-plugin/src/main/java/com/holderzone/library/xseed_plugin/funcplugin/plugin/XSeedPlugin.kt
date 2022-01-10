package com.holderzone.library.xseed_plugin.funcplugin.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/7
 */
fun Project.android(): BaseExtension {
    val android = project.extensions.findByType(BaseExtension::class.java)
    if (android != null) {
        return android
    } else {
        throw Exception("Project $name is not an Android project")
    }
}



class XSeedPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.android().registerTransform(XSeedTransform())
    }

}