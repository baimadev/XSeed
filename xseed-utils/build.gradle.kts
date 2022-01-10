import com.android.build.gradle.internal.res.processResources

plugins {
    id ("java-library")
    id ("kotlin")
    `maven-publish`
    kotlin("jvm")
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
        create<MavenPublication>("mavenJava"){
            groupId = "com.holderzone.library"
            artifactId = "xseed-utils"
            version = "1.0.5"
            //如果是war包填写web，如果是jar包填写java 当前java项目作为发布内容。
            from(components["java"])
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

//tasks {
//
//    processResources {
//        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//    }
//
//}
