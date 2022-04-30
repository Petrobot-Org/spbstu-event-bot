package ru.spbstu.eventbot

import com.github.kotlintelegrambot.Bot
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.apache.commons.mail.EmailException
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.GetApplicationsByCourseIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetExpiredCoursesFlowUseCase
import ru.spbstu.eventbot.email.EmailSender
import ru.spbstu.eventbot.telegram.createApplicationsXlsx
import ru.spbstu.eventbot.telegram.notifyCourseExpired

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
        return createApplicationsXlsx(result.applications)
    }
}
