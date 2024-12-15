package io.synergyspace.user.domain.service

import io.synergyspace.user.domain.model.entity.User

interface UserService {

    suspend fun create(user: User, passwordHash: String): User
    suspend fun findByUsername(username: String): User?
    suspend fun checkPassword(user: User, rawPassword: String): Boolean
}