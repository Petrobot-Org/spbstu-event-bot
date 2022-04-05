package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesUseCase
import ru.spbstu.eventbot.domain.usecases.GetCourseByIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetStudentByIdUseCase

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

fun CallbackQueryHandlerEnvironment.apply(courseId: Long, getCourseById: GetCourseByIdUseCase, chatId: Long, getStudentById: GetStudentByIdUseCase ) {
    val course = getCourseById(courseId) ?: return sendReply(Strings.NoSuchCourse)
    val student = getStudentById(chatId)?: return sendReply(Strings.NoSuchCourse)
    sendReply(
        text = Strings.apply(chatId, courseId),
        parseMode = ParseMode.MARKDOWN,
        replyMarkup = InlineKeyboardMarkup.createSingleButton(
            InlineKeyboardButton.CallbackData(Strings.SubmitApplication, "apply ${course.id}")
        )
    )
}
