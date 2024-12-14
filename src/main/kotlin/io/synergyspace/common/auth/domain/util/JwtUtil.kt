package io.synergyspace.common.auth.domain.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.synergyspace.auth.domain.model.entity.Credential
import java.util.Date

fun Application.getJwtAudience(): String = environment.config.property("ktor.jwt.audience").getString()

fun Application.getJwtIssuer(): String = environment.config.property("ktor.jwt.domain").getString()

fun Application.getJwtRealm(): String = environment.config.property("ktor.jwt.realm").getString()

fun Application.getJwtSecret(): String = environment.config.property("ktor.jwt.secret").getString()

fun Application.getJwtExpirationInMillis(): Long = environment.config.property("ktor.jwt.expirationTimeInMillis").getString().toLong()

fun Application.generateToken(credential: Credential): String = JWT.create()
    .withAudience(getJwtAudience())
    .withIssuer(getJwtIssuer())
    .withClaim("username", credential.login)
    .withClaim("userId", credential.userId.toString())
    .withExpiresAt(Date(System.currentTimeMillis() + getJwtExpirationInMillis()))
    .sign(Algorithm.HMAC256(getJwtSecret()))