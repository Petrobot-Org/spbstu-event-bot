package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesUseCase
import ru.spbstu.eventbot.domain.usecases.GetCourseByIdUseCase
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase

fun TextHandlerEnvironment.displayCourses(getAvailableCourses: GetAvailableCoursesUseCase) {
    val courses = getAvailableCourses()
    val buttons = courses.map {
        listOf(InlineKeyboardButton.CallbackData(it.title, "details ${it.id}"))
    }
    sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
}

fun CallbackQueryHandlerEnvironment.courseDetails(courseId: Long, getCourseById: GetCourseByIdUseCase) {
    val course = getCourseById(courseId) ?: return sendReply(Strings.NoSuchCourse)
    sendReply(
        text = Strings.courseDetails(course),
        parseMode = ParseMode.MARKDOWN,
        replyMarkup = InlineKeyboardMarkup.createSingleButton(
            InlineKeyboardButton.CallbackData(Strings.SubmitApplication, "apply ${course.id}")
        )
    )
}

fun CallbackQueryHandlerEnvironment.apply(
    chatId: Long,
    courseId: Long,
    submitApplicationUseCase: SubmitApplicationUseCase
) {
    val info = when (submitApplicationUseCase.invoke(chatId, courseId)) {
        is SubmitApplicationUseCase.Result.OK -> {
            Strings.GoodResult
        }
        is SubmitApplicationUseCase.Result.AlreadySubmitted -> {
            Strings.ApplicationAdded
        }
        is SubmitApplicationUseCase.Result.Expired -> {
            Strings.TimeOut
        }
        is SubmitApplicationUseCase.Result.NotRegistered -> {
            Strings.NotRegistered
        }
        is SubmitApplicationUseCase.Result.NoSuchCourse -> {
            Strings.CourseNotFound
        }
    }
    sendReply(
        text = info
    )
}
