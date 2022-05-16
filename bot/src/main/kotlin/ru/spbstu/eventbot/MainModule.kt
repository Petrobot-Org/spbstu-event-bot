package ru.spbstu.eventbot

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
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
import ru.spbstu.eventbot.telegram.ProvideState
import ru.spbstu.eventbot.telegram.flows.ClientRegistrationFlow
import ru.spbstu.eventbot.telegram.flows.CourseCreationFlow
import ru.spbstu.eventbot.telegram.flows.CoursesFlow
import ru.spbstu.eventbot.telegram.flows.RegistrationFlow

val mainModule = module(createdAtStart = true) {
    val appConfig = appConfig()
    val secrets = secrets()
    single { appConfig.zone }
    single { appConfig.operators }
    single { appConfig.groupFilters }
    single { secrets.emailSecrets }
    single { secrets.telegramToken }
    single { createAppDatabase(appConfig.jdbcString) }
    singleOf(::StudentRepositoryImpl) bind StudentRepository::class
    singleOf(::ApplicationRepositoryImpl) bind ApplicationRepository::class
    singleOf(::ClientRepositoryImpl) bind ClientRepository::class
    singleOf(::CourseRepositoryImpl) bind CourseRepository::class
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
    singleOf(::ClientRegistrationFlow)
    singleOf(::CoursesFlow)
    singleOf(::ProvidePermissions)
    singleOf(::ProvideState)
    singleOf(::GetExpiredCoursesFlowUseCase)
    singleOf(::GetMatchingStudentsUseCase)
    singleOf(::ExpiredCoursesCollector)
    singleOf(::NewCoursesCollector)
    singleOf(::CourseCreationFlow)
    singleOf(::EmailSender)
    singleOf(::Bot)
}
