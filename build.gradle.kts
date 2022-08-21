plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

allprojects {
    group = "xyz.luccboy.noobcloud"
    version = "1.0-SNAPSHOT"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(18))
        }
    }
}