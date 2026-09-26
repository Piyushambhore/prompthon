plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "com.jansaarthi.schemes"
version = "0.1.0"

application {
    mainClass.set("com.jansaarthi.schemes.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.7"
val exposedVersion = "0.46.0"
val logbackVersion = "1.4.14"

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")

    // Database — Exposed ORM + PostgreSQL (with H2 embedded fallback)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Environment variable loading
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
}

kotlin {
    jvmToolchain(17)
}
