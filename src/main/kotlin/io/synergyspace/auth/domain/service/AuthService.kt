package io.synergyspace.auth.domain.service

import io.synergyspace.auth.domain.model.entity.Credential
import io.synergyspace.user.domain.model.entity.User

interface AuthService {

    @Throws(Exception::class)
    suspend fun register(credential: Credential): User
    suspend fun login(credential: Credential): User?
}