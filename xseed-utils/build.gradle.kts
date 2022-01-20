
plugins {
    id("com.android.library")
    kotlin("android")
    //kotlin("jvm")
    `maven-publish`
}

android {

    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 21
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}


java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

//仓库地址
val MAVEN_NAME = "http://nexus.holderzone.cn/nexus/content/repositories/xseed-plugin/"
//本地仓库
//val MAVEN_NAME = "repo"
publishing {

    //需要发布的内容
    publications {

        //用于配置要发布的内容
        create<MavenPublication>("production") {
            groupId = "com.holderzone.library"
            artifactId = "xseed-utils"
            version = "1.2.3-alpha"
            //如果是war包填写web，如果是jar包填写java 当前java项目作为发布内容。
            //from(components["java"])
            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
        }
    }
    //目标仓库
    repositories {
        maven {
            //本地仓库
//            url = uri(layout.buildDirectory.dir(MAVEN_NAME))
            //maven仓库
            url = uri(MAVEN_NAME)
            isAllowInsecureProtocol = true
            credentials {
                username = "admin"
                password = "admin123"
            }
        }
    }
}
