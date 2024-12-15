package io.synergyspace.auth.infrastructure.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
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
import io.ktor.server.routing.route
import io.synergyspace.auth.domain.service.AuthService
import io.synergyspace.common.auth.domain.util.generateToken
import io.synergyspace.common.auth.domain.util.getJwtAudience
import io.synergyspace.common.auth.domain.util.getJwtIssuer
import io.synergyspace.common.auth.domain.util.getJwtRealm
import io.synergyspace.common.auth.domain.util.getJwtSecret
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.authRouting() {
    route("/auth") {
        val authService by inject<AuthService>()

        post("/register", {
            summary = "Register a new user"
            description = "Registers a new user with the given credentials."
            operationId = "registerUser"
            request {
                body<Credential> {
                    description = "User credentials for registration"
                    mediaTypes(ContentType.Application.Json)
                    required = true
                    example("Create new user") {
                        value = Credential(login = "testuser", passwordHash = "testpassword")
                    }
                }
            }
            response {
                HttpStatusCode.Created to {
                    description = "User successfully registered"
                    body<Map<String, String>> {
                        example("User created") {
                            value = mapOf(
                                "userId" to "9437696f-df12-4e09-8602-5a88023416cf",
                                "username" to "testuser",
                                "token" to "generated_jwt_token"
                            )
                        }
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "Invalid registration data provided"
                    body<Map<String, String>> {
                        example("Bad Request Error") {
                            value = mapOf(
                                "message" to "Invalid data"
                            )
                        }
                    }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server error"
                    body<Map<String, String>> {
                        example("Internal Server Error") {
                            value = mapOf(
                                "message" to "Something went wrong"
                            )
                        }
                    }
                }
            }
        }) {
            val credential = call.receive<Credential>()
            try {
                val user = authService.register(credential)
                val token = application.generateToken(
                    Credential(
                        login = user.username,
                        userId = user.id,
                    )
                )
                val responseBody: Map<String, String> = mapOf(
                    "userId" to user.id.toString(),
                    "username" to user.username,
                    "token" to token
                )

                call.respond(status = HttpStatusCode.Created, message = responseBody)
            } catch (e: BadRequestException) {
                call.respond(status = HttpStatusCode.BadRequest, message = e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = e.toString() ?: "Something went wrong"
                )
            }
        }

        post("/login", {
            summary = "User login"
            description = "Authenticates a user with the given credentials."
            operationId = "loginUser"
            request {
                body<Credential> {
                    description = "User credentials for login"
                    required = true
                    example("Login with credentials") {
                        value = Credential(login = "testuser", passwordHash = "testpassword")
                    }
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "User successfully logged in"
                    body<Map<String, String>> {
                        example("User logged in") {
                            value = mapOf(
                                "token" to "generated_jwt_token"
                            )
                        }
                    }
                }
                HttpStatusCode.Unauthorized to {
                    description = "Invalid credentials provided"
                    body<Map<String, String>> {
                        example("Unauthorized Error") {
                            value = mapOf(
                                "message" to "Invalid credentials"
                            )
                        }
                    }
                }
            }
        }) {
            val credential = call.receive<Credential>()
            val user = authService.login(credential)
            if (user != null) {
                val token = application.generateToken(
                    Credential(
                        login = user.username,
                        userId = user.id,
                    )
                )
                val responseBody = mapOf("token" to token)
                call.respond(status = HttpStatusCode.OK, message = responseBody)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        authenticate {
            get("/test_auth", {
                summary = "Test authenticated endpoint"
                description = "This is a protected endpoint that requires a valid JWT token."
                operationId = "testAuth"
                securitySchemeNames = listOf("JWT")
                response {
                    HttpStatusCode.OK to {
                        description = "Successfully authenticated"
                        body<String> {
                            example("Success") {
                                value = "Hello testuser, your id is 9437696f-df12-4e09-8602-5a88023416cf"
                            }
                        }
                    }
                    HttpStatusCode.Unauthorized to { description = "Unauthorized" }
                }
            }) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val userId = principal.payload.getClaim("userId").`as`(UUID::class.java)
                call.respondText("Hello $username, your id is $userId", ContentType.Text.Plain, HttpStatusCode.OK)
            }
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