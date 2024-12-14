package io.synergyspace.user.infrastructure.table

import org.jetbrains.exposed.dao.id.UUIDTable

object UserTable : UUIDTable() {
    val username = varchar("username", 63).uniqueIndex()
    val passwordHash = text("password_hash")
}