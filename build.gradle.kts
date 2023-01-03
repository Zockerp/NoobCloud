plugins {
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    group = "xyz.luccboy.noobcloud"
    version = "1.0-SNAPSHOT"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}