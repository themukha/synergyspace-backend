package io.synergyspace.idea.domain.service

import io.synergyspace.idea.domain.model.entity.Idea

interface IdeaService {

    suspend fun createIdea(idea: Idea): Idea
    suspend fun getAllIdeas(): List<Idea>
    suspend fun getIdeaById(id: Int): Idea?
    suspend fun updateIdea(idea: Idea): Idea?
    suspend fun deleteIdea(id: Int): Boolean
}