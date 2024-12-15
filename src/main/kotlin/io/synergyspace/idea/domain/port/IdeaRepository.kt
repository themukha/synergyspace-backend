package io.synergyspace.idea.domain.port

import io.synergyspace.idea.domain.model.entity.Idea

interface IdeaRepository {
    suspend fun create(idea: Idea): Idea
    suspend fun findAll(): List<Idea>
    suspend fun findById(id: Int): Idea?
    suspend fun update(idea: Idea): Idea?
    suspend fun delete(id: Int): Boolean
}