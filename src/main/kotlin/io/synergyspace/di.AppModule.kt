package io.synergyspace

import io.synergyspace.auth.application.service.AuthServiceImpl
import io.synergyspace.auth.domain.service.AuthService
import io.synergyspace.user.application.service.UserServiceImpl
import io.synergyspace.user.domain.repository.UserRepository
import io.synergyspace.user.domain.service.UserService
import io.synergyspace.user.infrastructure.repository.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserServiceImpl(get()) }
    single<AuthService> { AuthServiceImpl(get()) }
}