package io.synergyspace.idea.infrastructure.repository

import io.synergyspace.common.infrastructure.DatabaseFactory.dbQuery
import io.synergyspace.idea.domain.model.entity.Idea
import io.synergyspace.idea.domain.port.IdeaRepository
import io.synergyspace.idea.infrastructure.table.IdeaTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class IdeaRepositoryImpl : IdeaRepository {

    override suspend fun create(idea: Idea): Idea = dbQuery {
        val insertStatement = IdeaTable.insert {
            it[ownerId] = idea.ownerId
            it[title] = idea.title
            it[description] = idea.description
            it[tags] = idea.tags.joinToString(",")
            it[category] = idea.category
            it[status] = idea.status
            it[createdAt] = idea.createdAt
            it[updatedAt] = idea.updatedAt
        }
        insertStatement.resultedValues?.get(0)?.let(::rowToIdea)
            ?: throw Exception("Unable to insert new idea into database")
    }

    override suspend fun findAll(): List<Idea> = dbQuery {
        IdeaTable.selectAll().map(::rowToIdea)
    }

    override suspend fun findById(id: Int): Idea? = dbQuery {
        IdeaTable.selectAll().where(IdeaTable.id eq id)
            .map(::rowToIdea)
            .singleOrNull()
    }

    override suspend fun update(idea: Idea): Idea? = dbQuery {
        IdeaTable.update({ IdeaTable.id eq idea.id!! }) {
            it[ownerId] = idea.ownerId
            it[title] = idea.title
            it[description] = idea.description
            it[tags] = idea.tags.joinToString(",")
            it[category] = idea.category
            it[status] = idea.status
            it[updatedAt] = idea.updatedAt
        }
        findById(idea.id!!)
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        IdeaTable.deleteWhere { IdeaTable.id eq id } > 0
    }


    private fun rowToIdea(row: ResultRow): Idea = Idea(
        id = row[IdeaTable.id].value,
        ownerId = row[IdeaTable.ownerId].value,
        title = row[IdeaTable.title],
        description = row[IdeaTable.description],
        tags = row[IdeaTable.tags].split(",", " ").filter { it.isNotBlank() },
        category = row[IdeaTable.category],
        status = row[IdeaTable.status],
        createdAt = row[IdeaTable.createdAt],
        updatedAt = row[IdeaTable.updatedAt]
    )
}