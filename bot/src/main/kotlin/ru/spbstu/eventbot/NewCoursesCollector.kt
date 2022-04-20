package ru.spbstu.eventbot

import com.github.kotlintelegrambot.Bot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.apache.commons.mail.EmailException
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.CreateNewCourseUseCase
import ru.spbstu.eventbot.domain.usecases.GetApplicationsByCourseIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetExpiredCoursesFlowUseCase
import ru.spbstu.eventbot.email.EmailSender
import ru.spbstu.eventbot.telegram.Strings
import ru.spbstu.eventbot.telegram.notifyCourseExpired

class NewCoursesCollector(
    private val createNewCourseUseCase: CreateNewCourseUseCase
) {
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    fun start(bot: Bot) {
        coroutineScope.launch {
            createNewCourseUseCase.newCoursesFlow.collect {
                TODO("Отправить сообщение о новом курсе всем студентам")
            }
        }
    }
}
