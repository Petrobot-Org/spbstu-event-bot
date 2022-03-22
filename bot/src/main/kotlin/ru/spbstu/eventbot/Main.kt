package ru.spbstu.eventbot

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.spbstu.eventbot.data.repository.ApplicationRepositoryImpl
import ru.spbstu.eventbot.data.repository.UserRepositoryImpl
import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository
import ru.spbstu.eventbot.domain.usecases.RegisterUserUseCase
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase
import ru.spbstu.eventbot.telegram.Bot
import java.sql.SQLException

val mainModule = module {
    single<SqlDriver> {
        JdbcSqliteDriver("jdbc:sqlite:main.sqlite").also {
            try {
                AppDatabase.Schema.create(it)
            } catch (e: SQLException) {
                println("Schema has already been created")
            }
        }
    }
    single { AppDatabase(get()) }
    single<StudentRepository> { UserRepositoryImpl(get()) }
    single<ApplicationRepository> { ApplicationRepositoryImpl(get()) }
//    single { SubmitApplicationUseCase(get(), get()) }
    single { RegisterUserUseCase(get()) }
}

fun main(args: Array<String>) {
    startKoin {
        modules(mainModule)
    }
    Bot().start(args[0])
}
