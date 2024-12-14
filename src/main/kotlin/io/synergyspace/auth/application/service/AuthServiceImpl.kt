package io.synergyspace.auth.application.service

import io.ktor.server.plugins.BadRequestException
import io.synergyspace.auth.domain.model.entity.Credential
import io.synergyspace.auth.domain.service.AuthService
import io.synergyspace.user.domain.model.entity.User
import io.synergyspace.user.domain.service.UserService

class AuthServiceImpl(
    private val userService: UserService,
) : AuthService {

    override suspend fun register(credential: Credential): User {
        val existingUser = userService.findByUsername(credential.login)
        if (existingUser != null) {
            throw BadRequestException("User already exists")
        }
        if (credential.passwordHash == null) {
            throw BadRequestException("Password is empty")
        }
        return userService.create(User(username = credential.login), credential.passwordHash)
    }

    override suspend fun login(credential: Credential): User? {
        val user = userService.findByUsername(credential.login)
        if (user == null || credential.passwordHash == null || credential.passwordHash == "") {
            return null
        }
        val isPasswordCorrect = userService.checkPassword(user, credential.passwordHash)
        return if (isPasswordCorrect) user else null
    }
}