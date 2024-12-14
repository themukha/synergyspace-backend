package io.synergyspace.user.infrastructure.repository

import io.synergyspace.common.infrastructure.DatabaseFactory.dbQuery
import io.synergyspace.user.domain.model.entity.User
import io.synergyspace.user.domain.repository.UserRepository
import io.synergyspace.user.infrastructure.table.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import java.util.UUID

class UserRepositoryImpl : UserRepository {

    override suspend fun create(user: User, passwordHash: String): User = dbQuery {
        val insertStatement = UserTable.insert {
            it[UserTable.username] = user.username
            it[UserTable.passwordHash] = passwordHash
        }
        val result = insertStatement.resultedValues?.get(0)?.let(::rowToUser)
        result ?: throw Exception("User has not been created")
    }

    override suspend fun findByUsername(username: String): User? = dbQuery {
        UserTable.select(UserTable.username eq username)
            .map(::rowToUser)
            .singleOrNull()
    }

    override suspend fun findById(id: UUID): User? = dbQuery {
        UserTable.select(UserTable.id eq id)
            .map(::rowToUser)
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow): User = User(
        id = row[UserTable.id].value,
        username = row[UserTable.username],
        passwordHash = row[UserTable.passwordHash]
    )
}