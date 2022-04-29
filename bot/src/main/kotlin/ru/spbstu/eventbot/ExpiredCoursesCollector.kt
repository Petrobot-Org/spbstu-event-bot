package ru.spbstu.eventbot

import com.github.kotlintelegrambot.Bot
import io.github.evanrupert.excelkt.Sheet
import io.github.evanrupert.excelkt.workbook
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.apache.commons.mail.EmailException
import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.GetApplicationsByCourseIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetExpiredCoursesFlowUseCase
import ru.spbstu.eventbot.email.EmailSender
import ru.spbstu.eventbot.telegram.Strings
import ru.spbstu.eventbot.telegram.generateApplicationsTable
import ru.spbstu.eventbot.telegram.notifyCourseExpired
import java.io.ByteArrayOutputStream

private val logger = KotlinLogging.logger { }

class ExpiredCoursesCollector(
    private val emailSender: EmailSender,
    private val getExpiredCourses: GetExpiredCoursesFlowUseCase,
    private val getApplicationsByCourseId: GetApplicationsByCourseIdUseCase
) {
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    fun start(bot: Bot) {
        coroutineScope.launch {
            getExpiredCourses().collect {
                with(Permissions.App) {
                    val applicantsTable = createApplicationsTable(it.course) ?: return@with
                    launch {
                        notifyCourseExpired(it.course, bot, applicantsTable)
                    }
                    launch {
                        try {
                            emailSender.sendCourseExpired(it.course, applicantsTable)
                        } catch (e: EmailException) {
                            logger.error(e) { "Email not sent: $e" }
                        }
                    }
                    it.markAsSent()
                }
            }
        }
    }

    context(Permissions)
            private fun createApplicationsTable(course: Course): ByteArray? {
        val result = getApplicationsByCourseId(course.id) as? GetApplicationsByCourseIdUseCase.Result.OK ?: return null
        return generateApplicationsTable(result.applications)
    }
}
