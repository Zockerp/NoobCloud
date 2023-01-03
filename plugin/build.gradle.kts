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
    compileOnly("com.github.Minestom:Minestom:24cc458659")
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    // Netty - communication
    implementation("io.netty:netty-all:4.1.86.Final")
    // Database
    implementation("com.github.Luccboy:database-api:1.0.0-rc1")
    implementation("com.github.Luccboy:database-api:1.0.0-rc1")
    implementation("mysql:mysql-connector-java:8.0.31")
    // Jackson - config
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")
}

tasks.shadowJar {
    archiveFileName.set("NoobCloudPlugin.jar")
}