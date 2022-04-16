package ru.spbstu.eventbot

import com.github.kotlintelegrambot.Bot
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.GetExpiredCoursesFlowUseCase
import ru.spbstu.eventbot.email.EmailSender
import ru.spbstu.eventbot.telegram.notifyCourseExpired

class ExpiredCoursesCollector(
    private val emailSender: EmailSender,
    private val getExpiredCourses: GetExpiredCoursesFlowUseCase
) {
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    fun start(bot: Bot) {
        coroutineScope.launch {
            getExpiredCourses().collect {
                with(Permissions.App) {
                    val applicantsTable = createApplicationsTable(it.course)
                    notifyCourseExpired(it.course, bot, applicantsTable)
                    emailSender.sendCourseExpired(it.course, applicantsTable)
                    it.markAsSent()
                }
            }
        }
    }

    context(Permissions)
    private fun createApplicationsTable(course: Course): ByteArray {
        return "Здесь таблица"
            .split("")
            .joinToString(", ")
            .encodeToByteArray()
    }
}
