import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka") version "1.7.20"
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.Minestom:Minestom:eb06ba8664")
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
}

tasks.shadowJar {
    archiveFileName.set("NoobCloudAPI-${project.version}.jar")
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("NoobCloud")
            includes.from("Module.md")
        }
    }
}