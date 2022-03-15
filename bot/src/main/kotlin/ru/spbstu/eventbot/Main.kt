package ru.spbstu.eventbot

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.spbstu.eventbot.data.repository.ApplicationRepositoryImpl
import ru.spbstu.eventbot.data.repository.UserRepositoryImpl
import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.UserRepository
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase
import ru.spbstu.eventbot.telegram.Bot

val mainModule = module {
    single { AppDatabase(JdbcSqliteDriver("jdbc:sqlite:main.sqlite")) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ApplicationRepository> { ApplicationRepositoryImpl(get()) }
    single { SubmitApplicationUseCase(get(), get()) }
}

fun main(args: Array<String>) {
    startKoin{
        modules(mainModule)
    }
    Bot().start(args[0])
}