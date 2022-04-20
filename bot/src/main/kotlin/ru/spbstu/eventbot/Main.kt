package ru.spbstu.eventbot

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.spbstu.eventbot.data.createAppDatabase
import ru.spbstu.eventbot.data.repository.ApplicationRepositoryImpl
import ru.spbstu.eventbot.data.repository.ClientRepositoryImpl
import ru.spbstu.eventbot.data.repository.CourseRepositoryImpl
import ru.spbstu.eventbot.data.repository.StudentRepositoryImpl
import ru.spbstu.eventbot.domain.permissions.GetPermissionsUseCase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository
import ru.spbstu.eventbot.domain.usecases.*
import ru.spbstu.eventbot.email.EmailSender
import ru.spbstu.eventbot.telegram.Bot
import ru.spbstu.eventbot.telegram.ProvidePermissions
import ru.spbstu.eventbot.telegram.flows.*

val mainModule = module {
    val appConfig = appConfig()
    val secrets = secrets()
    single { appConfig.zone }
    single { appConfig.operators }
    single { createAppDatabase(appConfig.jdbcString) }
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<ApplicationRepository> { ApplicationRepositoryImpl(get()) }
    single<ClientRepository> { ClientRepositoryImpl(get()) }
    single<CourseRepository> { CourseRepositoryImpl(get()) }
    singleOf(::SubmitApplicationUseCase)
    singleOf(::RevokeApplicationUseCase)
    singleOf(::IsApplicationSubmittedUseCase)
    singleOf(::RegisterStudentUseCase)
    singleOf(::GetAvailableCoursesUseCase)
    singleOf(::GetCourseByIdUseCase)
    singleOf(::RegisterClientUseCase)
    singleOf(::GetApplicationsByCourseIdUseCase)
    singleOf(::GetClientCoursesUseCase)
    singleOf(::CreateNewCourseUseCase)
    singleOf(::GetMyClientsUseCase)
    singleOf(::GetPermissionsUseCase)
    singleOf(::RegistrationFlow)
    singleOf(::CourseCreationFlow)
    singleOf(::ClientRegistrationFlow)
    singleOf(::CoursesFlow)
    singleOf(::ProvidePermissions)
    singleOf(::GetExpiredCoursesFlowUseCase)
    singleOf(::ExpiredCoursesCollector)
    singleOf(::NewCoursesCollector)
    single { EmailSender(secrets.emailSecrets) }
    single { Bot(secrets.telegramToken, get(), get(), get(), get(), get()) }
}

fun main() {
    startKoin {
        modules(mainModule)
        val bot = koin.get<Bot>()
        val expiredCoursesCollector = koin.get<ExpiredCoursesCollector>()
        val newCoursesCollector = koin.get<NewCoursesCollector>()
        bot.start()
        expiredCoursesCollector.start(bot.bot)
        newCoursesCollector.start(bot.bot)
    }
}
