package ru.spbstu.eventbot

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ru.spbstu.eventbot.data.adapter.DateAdapter
import ru.spbstu.eventbot.data.entities.Course
import ru.spbstu.eventbot.data.repository.ApplicationRepositoryImpl
import ru.spbstu.eventbot.data.repository.ClientRepositoryImpl
import ru.spbstu.eventbot.data.repository.CourseRepositoryImpl
import ru.spbstu.eventbot.data.repository.StudentRepositoryImpl
import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.permissions.GetPermissionsUseCase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository
import ru.spbstu.eventbot.domain.usecases.*
import ru.spbstu.eventbot.telegram.Bot
import ru.spbstu.eventbot.telegram.ProvidePermissions
import ru.spbstu.eventbot.telegram.flows.ClientRegistrationFlow
import ru.spbstu.eventbot.telegram.flows.CourseCreationFlow
import ru.spbstu.eventbot.telegram.flows.RegistrationFlow
import java.sql.SQLException

val mainModule = module {
    val appConfig = appConfig()
    single { appConfig.zone }
    single { appConfig.operators }
    single<SqlDriver> {
        JdbcSqliteDriver(appConfig.jdbcString).also {
            try {
                AppDatabase.Schema.create(it)
            } catch (e: SQLException) {
                println("Schema has already been created")
            }
        }
    }
    single {
        AppDatabase(
            driver = get(),
            CourseAdapter = Course.Adapter(
                expiry_dateAdapter = DateAdapter()
            )
        )
    }
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<ApplicationRepository> { ApplicationRepositoryImpl(get()) }
    single<ClientRepository> { ClientRepositoryImpl(get()) }
    single<CourseRepository> { CourseRepositoryImpl(get()) }
    single { SubmitApplicationUseCase(get(), get()) }
    single { RegisterStudentUseCase(get()) }
    single { GetAvailableCoursesUseCase(get()) }
    single { GetCourseByIdUseCase(get()) }
    single { RegisterClientUseCase(get()) }
    single { GetApplicantsByCourseIdUseCase(get(), get()) }
    single { GetClientCoursesUseCase(get()) }
    single { CreateNewCourseUseCase(get(), get()) }
    single { GetMyClientsUseCase(get()) }
    single { GetPermissionsUseCase(get(), get()) }
    single { RegistrationFlow(get()) }
    single { CourseCreationFlow(get(), get(), get()) }
    single { ClientRegistrationFlow(get()) }
    single { ProvidePermissions(get()) }
}

fun main(args: Array<String>) {
    startKoin {
        modules(mainModule)
    }
    Bot().start(args[0])
}
