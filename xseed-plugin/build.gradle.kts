plugins {
    id ("java-gradle-plugin")
    id ("maven-publish")
}

val MAVEN_NAME = "http://nexus.holderzone.cn/nexus/content/repositories/xseed-plugin/"

gradlePlugin {
    plugins {

        create("standaloneGradlePlugins"){
            //插件名
            id = "xseed-plugin"
            implementationClass = "com.holderzone.library.xseed_plugin.XSeedPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava"){
            groupId = "com.holderzone.library"
            artifactId = "xseed-plugin"
            version = "1.0.1"
            from(components["java"])
        }
    }
    repositories {
        maven {
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
