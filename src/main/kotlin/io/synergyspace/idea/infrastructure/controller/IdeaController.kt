package io.synergyspace.idea.infrastructure.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.put
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.param
import io.ktor.server.routing.route
import io.synergyspace.idea.domain.model.entity.Idea
import io.synergyspace.idea.domain.service.IdeaService
import io.synergyspace.idea.infrastructure.table.IdeaTable.description
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.ideaRouting() {
    route("/idea"){
        val ideaService by inject<IdeaService>()

        authenticate {
            post({
                summary = "Create a new idea"
                description = "Creates a new idea associated with the authenticated user."
                operationId = "createIdea"
                securitySchemeNames = listOf("JWT")
                request {
                    body<Idea> {
                        description = "Idea object that needs to be added"
                        required = true
                        example("New Idea") {
                            value = Idea(
                                ownerId = UUID.fromString("9437696f-df12-4e09-8602-5a88023416cf"),
                                title = "My New Idea",
                                description = "This is a description of my new idea.",
                                tags = listOf("Backend", "Frontend", "Finance", "Tracker"),
                                category = "Application",
                            )
                        }
                    }
                }
                response {
                    HttpStatusCode.Created to {
                        description = "Idea created successfully"
                        body<Idea> {
                            example("Created Idea") {
                                value = Idea(
                                    id = 1,
                                    ownerId = UUID.fromString("9437696f-df12-4e09-8602-5a88023416cf"),
                                    title = "My New Idea",
                                    description = "This is a description of my new idea.",
                                    tags = listOf("Backend", "Frontend", "Finance", "Tracker"),
                                    category = "Application"
                                )
                            }
                        }
                    }
                    HttpStatusCode.BadRequest to { description = "Invalid input" }
                    HttpStatusCode.Unauthorized to { description = "Unauthorized" }
                }
            }) {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").`as`(UUID::class.java)

                val idea = call.receive<Idea>()
                try {
                    val newIdea = ideaService.createIdea(idea.copy(ownerId = userId))
                    call.respond(status = HttpStatusCode.Created, message = newIdea)
                } catch (e: BadRequestException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
                }

            }

            get({
                summary = "Get all ideas"
                description = "Retrieves all ideas."
                operationId = "getAllIdeas"
                securitySchemeNames = listOf("JWT")
                response {
                    HttpStatusCode.OK to {
                        description = "Successfully retrieved list of ideas"
                        body<List<Idea>>() {
                            example("List of Ideas") {
                                value = listOf(
                                    Idea(
                                        id = 1,
                                        ownerId = UUID.fromString("9437696f-df12-4e09-8602-5a88023416cf"),
                                        title = "Idea 1",
                                        description = "Description 1",
                                        tags = listOf("tag1", "tag2"),
                                        category = "Application",
                                    ),
                                    Idea(
                                        id = 2,
                                        ownerId = UUID.fromString("49469fc9-c1b8-486a-8b44-cc721f08f235"),
                                        title = "Idea 2",
                                        description = "Description 2",
                                        tags = listOf("tag3", "tag4"),
                                        category = "Science"
                                    )
                                )
                            }
                        }
                    }
                    HttpStatusCode.Unauthorized to { description = "Unauthorized" }
                }
            }) {
                val ideas = ideaService.getAllIdeas()
                call.respond(HttpStatusCode.OK, ideas)
            }

            get("/{id}", {
                summary = "Get idea by ID"
                description = "Retrieves a specific idea by its ID."
                operationId = "getIdeaById"
                securitySchemeNames = listOf("JWT")
                request {
                    pathParameter<Int>("id") {
                        description = "ID of the idea to retrieve"
                        required = true
                        example("1") {
                            value = 1
                            summary = "Example of ID of the idea to retrieve"
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Successfully retrieved the idea"
                        body<Idea> {
                            example("Retrieved Idea") {
                                value = Idea(
                                    id = 1,
                                    ownerId = UUID.fromString("9437696f-df12-4e09-8602-5a88023416cf"),
                                    title = "Idea 1",
                                    description = "Description 1",
                                    tags = listOf("tag1", "tag2"),
                                    category = "Application"
                                )
                            }
                        }
                    }
                    HttpStatusCode.BadRequest to { description = "Invalid ID supplied" }
                    HttpStatusCode.Unauthorized to { description = "Unauthorized" }
                    HttpStatusCode.NotFound to { description = "Idea not found" }
                }
            }) {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val idea = ideaService.getIdeaById(id)
                if (idea == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(idea)
                }
            }

            put("/{id}", {
                summary = "Update an idea"
                description = "Updates an existing idea. Only the owner of the idea can update it."
                operationId = "updateIdea"
                securitySchemeNames = listOf("JWT")
                request {
                    pathParameter<Int>("id") {
                        description = "ID of the idea to update"
                        required = true
                        example("1") {
                            value = 1
                            summary = "Example of ID of the idea to update"
                        }
                    }
                    body<Idea> {
                        description = "Updated idea object"
                        required = true
                        example("Updated Idea") {
                            value = Idea(
                                id = 1,
                                ownerId = UUID.fromString("9437696f-df12-4e09-8602-5a88023416cf"),
                                title = "Updated Idea Title",
                                description = "Updated description",
                                tags = listOf("updatedTag1", "updatedTag2"),
                                category = "Updated Category"
                            )
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Idea updated successfully"
                        body<Idea> {
                            example("Updated Idea") {
                                value = Idea(
                                    id = 1,
                                    ownerId = UUID.fromString("9437696f-df12-4e09-8602-5a88023416cf"),
                                    title = "Updated Idea Title",
                                    description = "Updated description",
                                    tags = listOf("updatedTag1", "updatedTag2"),
                                    category = "Updated Category"
                                )
                            }
                        }
                    }
                    HttpStatusCode.BadRequest to { description = "Invalid input" }
                    HttpStatusCode.Unauthorized to { description = "Unauthorized" }
                    HttpStatusCode.Forbidden to { description = "Forbidden - User is not the owner of the idea" }
                    HttpStatusCode.NotFound to { description = "Idea not found" }
                }
            }) {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").`as`(UUID::class.java)

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@put
                }
                val idea = call.receive<Idea>()

                if (ideaService.getIdeaById(id)?.ownerId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "You are not allowed to change this idea")
                    return@put
                }

                try {
                    val updatedIdea = ideaService.updateIdea(idea.copy(id = id, ownerId = userId))
                    if (updatedIdea == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respond(updatedIdea)
                    }
                } catch (e: BadRequestException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
                }
            }

            delete("/{id}", {
                summary = "Delete an idea"
                description = "Deletes an idea by its ID. Only the owner of the idea can delete it."
                operationId = "deleteIdea"
                securitySchemeNames = listOf("JWT")
                request {
                    pathParameter<Int>("id") {
                        description = "ID of the idea to delete"
                        required = true
                        example("1") {
                            value = 1
                            summary = "Example of ID of the idea to delete"
                        }
                    }
                }
                response {
                    HttpStatusCode.NoContent to { description = "Idea deleted successfully" }
                    HttpStatusCode.BadRequest to { description = "Invalid ID supplied" }
                    HttpStatusCode.Unauthorized to { description = "Unauthorized" }
                    HttpStatusCode.Forbidden to { description = "Forbidden - User is not the owner of the idea" }
                    HttpStatusCode.NotFound to { description = "Idea not found" }
                }
            }) {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").`as`(UUID::class.java)

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@delete
                }

                if (ideaService.getIdeaById(id)?.ownerId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "You are not allowed to delete this idea")
                    return@delete
                }

                val isDeleted = ideaService.deleteIdea(id)
                if (isDeleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }

            }
        }
    }
}