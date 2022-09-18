dependencies {
    // NoobCloud-API and -Library
    implementation(project(":library"))
    // Kotlin - coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Log4j2 - logging
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    // Jackson - config
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.13.4")
    // Netty - communication
    implementation("io.netty:netty-all:4.1.82.Final")
    // JLine3 - terminal
    implementation("org.jline:jline-reader:3.21.0")
    implementation("org.jline:jline-terminal:3.21.0")
    implementation("org.jline:jline-terminal-jansi:3.21.0")
    implementation("org.jline:jline-console:3.21.0")
}

tasks.shadowJar {
    archiveFileName.set("${project.parent!!.name}-${project.parent!!.version}.jar")
    manifest.attributes(Pair("Main-Class", "xyz.luccboy.noobcloud.NoobCloudLauncherKt"))
    manifest.attributes(
        "Main-Class" to "xyz.luccboy.noobcloud.NoobCloudLauncherKt",
        "Multi-Release" to true
    )
}