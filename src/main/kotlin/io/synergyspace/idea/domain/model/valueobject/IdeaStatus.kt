package io.synergyspace.idea.domain.model.valueobject

import kotlinx.serialization.Serializable

@Serializable
enum class IdeaStatus {
    DRAFT,
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
}