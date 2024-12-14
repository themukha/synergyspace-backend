package io.synergyspace.user.application.service

import io.synergyspace.common.auth.domain.util.hashPassword
import io.synergyspace.common.auth.domain.util.verifyPassword
import io.synergyspace.user.domain.model.entity.User
import io.synergyspace.user.domain.repository.UserRepository
import io.synergyspace.user.domain.service.UserService

class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override suspend fun create(user: User, passwordHash: String): User =
        userRepository.create(user, hashPassword(passwordHash))

    override suspend fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    override suspend fun checkPassword(user: User, rawPassword: String): Boolean {
        val dbUser = userRepository.findById(user.id!!)

        return if (dbUser == null) {
            false
        } else {
            verifyPassword(rawPassword, dbUser.passwordHash)
        }
    }
}