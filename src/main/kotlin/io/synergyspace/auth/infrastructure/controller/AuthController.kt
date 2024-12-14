package io.synergyspace.auth.infrastructure.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.synergyspace.auth.domain.model.entity.Credential
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.synergyspace.auth.domain.service.AuthService
import io.synergyspace.common.auth.domain.util.generateToken
import io.synergyspace.common.auth.domain.util.getJwtAudience
import io.synergyspace.common.auth.domain.util.getJwtIssuer
import io.synergyspace.common.auth.domain.util.getJwtRealm
import io.synergyspace.common.auth.domain.util.getJwtSecret
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.authRouting() {
    val authService by inject<AuthService>()

    post("/register") {
        val credential = call.receive<Credential>()
        try {
            val user = authService.register(credential)
            val token = application.generateToken(Credential(
                login = user.username,
                userId = user.id,
            ))
            val responseBody: Map<String, String> = mapOf(
                "userId" to user.id.toString(),
                "username" to user.username,
                "token" to token
            )

            call.respond(status = HttpStatusCode.Created, message = responseBody)
        } catch (e: BadRequestException) {
            call.respond(status = HttpStatusCode.BadRequest, message = e.message ?: "Invalid data")
        } catch (e: Exception) {
            call.respond(status = HttpStatusCode.InternalServerError, message = e.toString() ?: "Something went wrong")
        }
    }

    post("/login") {
        val credential = call.receive<Credential>()
        val user = authService.login(credential)
        val responseBody = mapOf("token" to application.generateToken(credential))
        if (user != null) {
            call.respond(status = HttpStatusCode.OK, message = responseBody)
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    authenticate {
        get("/test_auth") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal.payload.getClaim("userId").`as`(UUID::class.java)
            call.respondText("Hello $username, your id is $userId", ContentType.Text.Plain, HttpStatusCode.OK)
        }
    }
}

fun Application.configureAuth() {
    install(Authentication) {
        jwt {
            realm = this@configureAuth.getJwtRealm()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(this@configureAuth.getJwtSecret()))
                    .withAudience(this@configureAuth.getJwtAudience())
                    .withIssuer(this@configureAuth.getJwtIssuer())
                    .build()
            )
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                if (username != "" && username != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}