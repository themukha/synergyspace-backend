package io.synergyspace.idea.infrastructure.table

import io.synergyspace.idea.domain.model.valueobject.IdeaStatus
import io.synergyspace.user.infrastructure.table.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object IdeaTable : IntIdTable() {
    val ownerId = reference("owner_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val description = text("description")
    val tags = text("tags")
    val category = varchar("category", 63)
    val status = enumerationByName<IdeaStatus>("status", 63)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}