package io.synergyspace.user.domain.repository

import io.synergyspace.user.domain.model.entity.User
import java.util.UUID

interface UserRepository {

    suspend fun create(user: User, passwordHash: String): User

    suspend fun findByUsername(username: String): User?

    suspend fun findById(id: UUID): User?
}