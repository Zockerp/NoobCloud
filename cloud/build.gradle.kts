dependencies {
    // NoobCloud-API and -Library
    implementation(project(":library"))
    // Kotlin - coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Log4j2 - logging
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    // Jackson - config
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.14.1")
    // Netty - communication
    implementation("io.netty:netty-all:4.1.86.Final")
    // Terminal
    implementation("net.minecrell:terminalconsoleappender:1.3.0")
    implementation("org.jline:jline-terminal-jansi:3.21.0")
    implementation("org.jline:jline-console:3.21.0")
}

tasks.shadowJar {
    archiveFileName.set("${project.parent!!.name}-${project.parent!!.version}.jar")
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer())
    manifest.attributes(Pair("Main-Class", "xyz.luccboy.noobcloud.NoobCloudLauncherKt"))
    manifest.attributes(
        "Main-Class" to "xyz.luccboy.noobcloud.NoobCloudLauncherKt",
        "Multi-Release" to true
    )
}