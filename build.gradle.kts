// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven{
            //远端仓库
            setUrl("http://nexus.holderzone.cn/nexus/content/repositories/xseed-plugin/")
            //本地仓库
//            setUrl("./repo")

            isAllowInsecureProtocol = true
        }
    }

    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.3")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath ("com.holderzone.library:xseed-plugin:1.0.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}