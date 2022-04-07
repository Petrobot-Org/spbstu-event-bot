package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.GetApplicantsByCourseIdUseCase
import ru.spbstu.eventbot.domain.usecases.GetClientCoursesUseCase
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesUseCase
import ru.spbstu.eventbot.domain.usecases.GetCourseByIdUseCase

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

context(Permissions)
fun TextHandlerEnvironment.displayApplicants(getClientCourses: GetClientCoursesUseCase) {
    val result = getClientCourses()
    when (result) {
        is GetClientCoursesUseCase.Result.OK -> {
            val buttons = result.courses.map {
                listOf(InlineKeyboardButton.CallbackData(it.title, "applicants ${it.id}"))
            }
            sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
        }
        GetClientCoursesUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
    }
}

context(Permissions)
fun CallbackQueryHandlerEnvironment.applicantsInfo(courseId: Long, getApplicants: GetApplicantsByCourseIdUseCase) {
    val result = getApplicants(courseId)
    when (result) {
        is GetApplicantsByCourseIdUseCase.Result.OK -> {
            if (result.applicants.isEmpty()) {
                sendReply(Strings.NoApplicants)
                return
            }
            sendReply(
                text = Strings.applicantsInfo(result.applicants),
                parseMode = ParseMode.MARKDOWN
            )
        }
        GetApplicantsByCourseIdUseCase.Result.NoSuchCourse -> sendReply(Strings.NoSuchCourse)
        GetApplicantsByCourseIdUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
    }
}
