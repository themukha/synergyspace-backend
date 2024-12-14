package io.synergyspace.user.domain.model.entity

import java.util.UUID

data class User(
    val id: UUID? = null,
    val username: String,
    val passwordHash: String = "",
)
