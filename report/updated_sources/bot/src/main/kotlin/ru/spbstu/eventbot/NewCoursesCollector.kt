package ru.spbstu.eventbot

import com.github.kotlintelegrambot.Bot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.spbstu.eventbot.domain.usecases.CreateNewCourseUseCase
import ru.spbstu.eventbot.domain.usecases.GetMatchingStudentsUseCase
import ru.spbstu.eventbot.telegram.notifyNewCourse

class NewCoursesCollector(
    private val createNewCourseUseCase: CreateNewCourseUseCase,
    private val getMatchingStudents: GetMatchingStudentsUseCase
) {
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    fun start(bot: Bot) {
        coroutineScope.launch {
            createNewCourseUseCase.newCoursesFlow.collect {
                notifyNewCourse(it, bot, getMatchingStudents(it.groupMatcher))
            }
        }
    }
}
