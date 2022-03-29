package ru.spbstu.eventbot

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.spbstu.eventbot.data.adapter.DateAdapter
import ru.spbstu.eventbot.data.entities.Course
import ru.spbstu.eventbot.data.repository.ApplicationRepositoryImpl
import ru.spbstu.eventbot.data.repository.FakeCourseRepositoryImpl
import ru.spbstu.eventbot.data.repository.StudentRepositoryImpl
import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesUseCase
import ru.spbstu.eventbot.domain.usecases.GetCourseByIdUseCase
import ru.spbstu.eventbot.domain.usecases.RegisterStudentUseCase
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase
import ru.spbstu.eventbot.telegram.Bot
import ru.spbstu.eventbot.telegram.UserPermissions
import java.io.File
import java.sql.SQLException

val mainModule = module {
    single { UserPermissions(File("operators.txt")) }
    single<SqlDriver> {
        JdbcSqliteDriver("jdbc:sqlite:main.sqlite").also {
            try {
                AppDatabase.Schema.create(it)
            } catch (e: SQLException) {
                println("Schema has already been created")
            }
        }
    }
    single { AppDatabase(driver = get(), CourseAdapter = Course.Adapter(expiry_dateAdapter = DateAdapter())) }
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<ApplicationRepository> { ApplicationRepositoryImpl(get()) }
    single<CourseRepository> { FakeCourseRepositoryImpl() }
    single { SubmitApplicationUseCase(get(), get()) }
    single { RegisterStudentUseCase(get()) }
    single { GetAvailableCoursesUseCase(get()) }
    single { GetCourseByIdUseCase(get()) }
}

fun main(args: Array<String>) {
    startKoin {
        modules(mainModule)
    }
    Bot().start(args[0])
}
