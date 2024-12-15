package io.synergyspace

import io.synergyspace.auth.application.service.AuthServiceImpl
import io.synergyspace.auth.domain.service.AuthService
import io.synergyspace.idea.application.service.IdeaServiceImpl
import io.synergyspace.idea.domain.port.IdeaRepository
import io.synergyspace.idea.domain.service.IdeaService
import io.synergyspace.idea.infrastructure.repository.IdeaRepositoryImpl
import io.synergyspace.user.application.service.UserServiceImpl
import io.synergyspace.user.domain.repository.UserRepository
import io.synergyspace.user.domain.service.UserService
import io.synergyspace.user.infrastructure.repository.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    // Users, authentication
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserServiceImpl(get()) }
    single<AuthService> { AuthServiceImpl(get()) }

    // Ideas
    single<IdeaRepository> { IdeaRepositoryImpl() }
    single<IdeaService> { IdeaServiceImpl(get()) }
}