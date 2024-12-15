val kotlinVersion: String by project
val logbackVersion: String by project
val kotlinxVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val auth0Version: String by project
val bcryptVersion: String by project
val koinVersion: String by project
val hikariVersion: String by project
val smileySwaggerVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
    id("com.google.cloud.tools.jib") version "3.4.4"
    application
}

group = "io.synergyspace"
version = "0.0.1-snapshot"


ktor {
    fatJar {
        archiveFileName.set("synergyspace.jar")
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
    }
    to {
        image = "georgymukha/synergyspace:${project.version}"
    }
    container {
        mainClass = application.mainClass.get()
        ports = listOf("8080/tcp")
        jvmFlags = application.applicationDefaultJvmArgs.toList()
    }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor and swagger
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.github.smiley4:ktor-swagger-ui:$smileySwaggerVersion")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxVersion")

    // Exposed (ORM)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // PostgreSQL Driver and HikariCP
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // JWT
    implementation("com.auth0:java-jwt:$auth0Version")

    // Для удобного хэширования паролей
    implementation("at.favre.lib:bcrypt:$bcryptVersion")

    // Koin (DI)
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // KMongo - MongoDB client for the future if needed
    // implementation("org.litote.kmongo:kmongo-coroutine:4.11.0")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
