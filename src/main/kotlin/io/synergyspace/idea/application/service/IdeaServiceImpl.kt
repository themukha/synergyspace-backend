package io.synergyspace.idea.application.service

import io.synergyspace.idea.domain.model.entity.Idea
import io.synergyspace.idea.domain.port.IdeaRepository
import io.synergyspace.idea.domain.service.IdeaService


class IdeaServiceImpl(
    private val ideaRepository: IdeaRepository
) : IdeaService {

    override suspend fun createIdea(idea: Idea): Idea = ideaRepository.create(idea)

    override suspend fun getAllIdeas(): List<Idea> = ideaRepository.findAll()

    override suspend fun getIdeaById(id: Int): Idea? = ideaRepository.findById(id)

    override suspend fun updateIdea(idea: Idea): Idea? = ideaRepository.update(idea)

    override suspend fun deleteIdea(id: Int): Boolean = ideaRepository.delete(id)
}