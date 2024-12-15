package io.synergyspace

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSyntaxHighlight
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.synergyspace.auth.infrastructure.controller.authRouting
import io.synergyspace.auth.infrastructure.controller.configureAuth
import io.synergyspace.common.infrastructure.AppSerializersModule
import io.synergyspace.common.infrastructure.DatabaseFactory
import io.synergyspace.idea.infrastructure.controller.ideaRouting
import io.synergyspace.idea.infrastructure.table.IdeaTable
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
    configureSwagger()

    DatabaseFactory.init(environment.config)

    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            UserTable,
            IdeaTable,
        )
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

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        authRouting()
        ideaRouting()
        get("/") {
            call.respondText("Welcome to SynergySpace", contentType = ContentType.Text.Plain)
        }
        route("openapi.json") {
            openApiSpec()
        }
        route("swagger-ui") {
            swaggerUI("/openapi.json")
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

private fun Application.configureSwagger() {
    install(SwaggerUI) {
        info {
            title = "SynergySpace API"
            version = "latest"
            description = "API to work with SynergySpace"
            termsOfService = "synergyspace.io"
            contact {
                name = "George Mukha"
                url = "https://t.me/themukha"
                email = "george@themukha.tech"
            }
            license {
                name = "Apache 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
                identifier = "Apache-2.0"
            }
        }
        swagger {
            displayOperationId = true
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.IDEA
            withCredentials = true
        }
        security {
            defaultUnauthorizedResponse {
                description = "Username, password or token is invalid"
            }
            defaultSecuritySchemeNames("JWT")
            securityScheme("JWT") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "jwt"
            }
        }
        tags {
            tagGenerator = { url -> listOf(url.firstOrNull() ?: "default") }
        }
    }

}
