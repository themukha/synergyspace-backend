package io.synergyspace.auth.domain.model.entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Credential(
    val login: String,
    @Contextual
    val userId: UUID? = null,
    val passwordHash: String? = null,
)
