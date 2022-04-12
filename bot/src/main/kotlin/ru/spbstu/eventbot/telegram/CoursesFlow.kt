package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.*

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
        replyMarkup = applyReplyMarkup(course.id)
    )
}

context(Permissions)
fun CallbackQueryHandlerEnvironment.apply(
    courseId: Long,
    submitApplicationUseCase: SubmitApplicationUseCase
) {
    val info = when (submitApplicationUseCase.invoke(courseId)) {
        is SubmitApplicationUseCase.Result.OK -> {
            bot.editMessageReplyMarkup(
                chatId = ChatId.fromId(chatId),
                messageId = callbackQuery.message?.messageId,
                replyMarkup = revokeReplyMarkup(courseId)
            )
            return
        }
        is SubmitApplicationUseCase.Result.AlreadySubmitted -> {
            Strings.AlreadyApplied
        }
        is SubmitApplicationUseCase.Result.Expired -> {
            Strings.CourseExpired
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

context(Permissions)
fun CallbackQueryHandlerEnvironment.revoke(
    courseId: Long,
    revokeApplication: RevokeApplicationUseCase
) {
    val info = when (revokeApplication(courseId)) {
        is RevokeApplicationUseCase.Result.OK -> {
            bot.editMessageReplyMarkup(
                chatId = ChatId.fromId(chatId),
                messageId = callbackQuery.message?.messageId,
                replyMarkup = applyReplyMarkup(courseId)
            )
            return
        }
        RevokeApplicationUseCase.Result.NotRegistered -> {
            Strings.NotRegistered
        }
    }
    sendReply(
        text = info
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

private fun revokeReplyMarkup(courseId: Long): ReplyMarkup {
    return InlineKeyboardMarkup.createSingleButton(
        InlineKeyboardButton.CallbackData(Strings.RevokeApplication, "revoke $courseId")
    )
}

private fun applyReplyMarkup(courseId: Long): ReplyMarkup {
    return InlineKeyboardMarkup.createSingleButton(
        InlineKeyboardButton.CallbackData(Strings.SubmitApplication, "apply $courseId")
    )
}
