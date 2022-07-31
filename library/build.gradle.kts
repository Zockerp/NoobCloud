plugins {
    id("org.jetbrains.kotlin.plugin.noarg") version("1.7.0")
}

dependencies {
    // Netty - communication
    implementation("io.netty:netty-all:4.1.79.Final")
    // Guava
    implementation("com.google.guava:guava:31.1-jre")
}

noArg {
    annotation("xyz.luccboy.noobcloud.library.annotations.NoArg")
}

tasks.shadowJar {
    archiveFileName.set("NoobCloudLibrary-${project.version}.jar")
}