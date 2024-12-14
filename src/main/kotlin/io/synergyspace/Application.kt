package io.synergyspace

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.synergyspace.auth.infrastructure.controller.authRouting
import io.synergyspace.auth.infrastructure.controller.configureAuth
import io.synergyspace.common.infrastructure.AppSerializersModule
import io.synergyspace.common.infrastructure.DatabaseFactory
import io.synergyspace.user.infrastructure.table.UserTable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)

//    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
}

fun Application.module() {
    val isDevelopment = environment.config.property("ktor.deployment.environment").getString() == "dev"

    configureLogging(isDevelopment)

    DatabaseFactory.init(environment.config)

    transaction {
        SchemaUtils.createMissingTablesAndColumns(UserTable)
    }

    install(Koin) {
        modules(appModule)
    }

    configureAuth()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            serializersModule = AppSerializersModule
        })
    }

    routing {
        authRouting()
        get("/") {
            call.respondText("Welcome to SynergySpace", contentType = ContentType.Text.Plain)
        }
    }
}

private fun Application.configureLogging(isDevelopment: Boolean) {
    val configFile = if (isDevelopment) "logback-dev.xml" else "logback.xml"
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    context.reset()
    JoranConfigurator().apply {
        this.context = context
        doConfigure(environment.classLoader.getResourceAsStream(configFile))
    }
}
