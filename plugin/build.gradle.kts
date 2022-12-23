plugins {
    kotlin("kapt")
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Kotlin - coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // NoobCloud-API and -Library
    implementation(project(":library"))
    implementation(project(":api"))
    // Minestom and Velocity
    compileOnly("com.github.Minestom:Minestom:eb06ba8664")
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    // Netty - communication
    implementation("io.netty:netty-all:4.1.86.Final")
    // Database
    implementation("com.github.Heliumdioxid.database-api:mysql:v1.0.0-rc1")
    implementation("mysql:mysql-connector-java:8.0.31")
    // Jackson - config
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")
}

tasks.shadowJar {
    archiveFileName.set("NoobCloudPlugin.jar")
}