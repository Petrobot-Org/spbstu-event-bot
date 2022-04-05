package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.usecases.GetApplicantsByCourseIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesByClientIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesUseCase
import ru.spbstu.eventbot.domain.usecases.GetCourseByIdUseCase

fun TextHandlerEnvironment.displayCourses(getAvailableCourses: GetAvailableCoursesUseCase) {
    val courses = getAvailableCourses()
    val buttons = courses.map {
        listOf(InlineKeyboardButton.CallbackData(it.title, "details ${it.id}"))
    }
    sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
}

fun TextHandlerEnvironment.displayApplicants(getAvailableCoursesByClientId: GetAvailableCoursesByClientIdUseCase) {
    val courses = getAvailableCoursesByClientId(message.chat.id)/////id заказчика
    val buttons = courses.map {
        listOf(InlineKeyboardButton.CallbackData(it.title, "applicants ${it.id}"))
    }
    sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
}

fun CallbackQueryHandlerEnvironment.applicantsInfo(courseId: Long,getApplicants: GetApplicantsByCourseIdUseCase ) {
    val applicants = getApplicants(courseId) ?: return sendReply(Strings.NoApplicants)
    sendReply(
        text = Strings.applicantsInfo(applicants),
        parseMode = ParseMode.MARKDOWN
    )
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
