plugins {
    id ("java-gradle-plugin")
    `maven-publish`
    kotlin("jvm")
}

//仓库地址
val MAVEN_NAME = "http://nexus.holderzone.cn/nexus/content/repositories/xseed-plugin/"
//本地仓库
//val MAVEN_NAME = "repo"

gradlePlugin {
    plugins {

        create("standaloneGradlePlugins"){
            //插件名
            id = "xseed-plugin"
            implementationClass = "com.holderzone.library.xseed_plugin.plugin.XSeedPlugin"
        }
    }

}



dependencies {
    implementation("com.android.tools.build:gradle:7.0.3")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.2")
    compileOnly("org.ow2.asm:asm-tree:9.2")
}

sourceSets {
    main {

        java {
            srcDirs("src/main/java")
        }

        resources {
            srcDirs("src/main/resources")
        }
    }
}


publishing {
    //需要发布的内容
    publications {
        //用于配置要发布的内容
        create<MavenPublication>("mavenJava"){
            groupId = "com.holderzone.library"
            artifactId = "xseed-plugin"
            version = "1.2.14-alpha"
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

tasks {

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

}


