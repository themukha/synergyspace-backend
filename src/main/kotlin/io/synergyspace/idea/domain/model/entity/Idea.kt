package io.synergyspace.idea.domain.model.entity

import io.synergyspace.idea.domain.model.valueobject.IdeaStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class Idea(
    val id: Int? = null,
    @Contextual
    val ownerId: UUID,
    val title: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val category: String,
    val status: IdeaStatus = IdeaStatus.DRAFT,
    @Contextual
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Contextual
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
