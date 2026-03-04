package di

import org.koin.dsl.module
import repositories.ChatRepository
import repositories.ChatRepositoryImpl
import repositories.UserRepository
import repositories.UserRepositoryImpl
import services.AuthService
import services.ChatService
import services.UserService

val appModule = module {
    single<UserRepository>{ UserRepositoryImpl() }
    single{ UserService(get()) }
    single<ChatRepository> { ChatRepositoryImpl() }
    single { ChatService(get()) }
    single { AuthService(get()) }
}