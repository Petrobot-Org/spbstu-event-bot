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

context(Permissions)
fun CallbackQueryHandlerEnvironment.courseDetails(
    courseId: Long,
    getCourseById: GetCourseByIdUseCase,
    isApplicationSubmitted: IsApplicationSubmittedUseCase
) {
    val course = getCourseById(courseId) ?: return sendReply(Strings.NoSuchCourse)
    sendReply(
        text = Strings.courseDetails(course),
        parseMode = ParseMode.MARKDOWN,
        replyMarkup = detailsReplyMarkup(courseId, isApplicationSubmitted)
    )
}

context(Permissions)
fun CallbackQueryHandlerEnvironment.apply(
    courseId: Long,
    setState: (ChatState) -> Unit,
    submitApplicationUseCase: SubmitApplicationUseCase,
    isApplicationSubmitted: IsApplicationSubmittedUseCase
) {
    when (val result = submitApplicationUseCase.invoke(courseId)) {
        is SubmitApplicationUseCase.Result.OK -> {}
        is SubmitApplicationUseCase.Result.AlreadySubmitted -> {
            sendReply(Strings.AlreadyApplied)
        }
        is SubmitApplicationUseCase.Result.Expired -> {
            sendReply(Strings.CourseExpired)
        }
        is SubmitApplicationUseCase.Result.NotRegistered -> {
            startRegistration(setState)
        }
        is SubmitApplicationUseCase.Result.NoSuchCourse -> {
            sendReply(Strings.CourseNotFound)
        }
        is SubmitApplicationUseCase.Result.AdditionalInfoRequired -> {
            sendReply(result.question)
            setState(ChatState.AdditionalInfoRequested(courseId, callbackQuery.message?.messageId))
        }
    }
    bot.editMessageReplyMarkup(
        chatId = ChatId.fromId(chatId),
        messageId = callbackQuery.message?.messageId,
        replyMarkup = detailsReplyMarkup(courseId, isApplicationSubmitted)
    )
}

// TODO: Deal with code duplication
context(Permissions)
fun TextHandlerEnvironment.handleAdditionalInfo(
    state: ChatState.AdditionalInfoRequested,
    setState: (ChatState) -> Unit,
    submitApplicationUseCase: SubmitApplicationUseCase,
    isApplicationSubmitted: IsApplicationSubmittedUseCase
) {
    setState(ChatState.Empty)
    when (val result = submitApplicationUseCase(state.courseId, text)) {
        is SubmitApplicationUseCase.Result.OK -> {}
        is SubmitApplicationUseCase.Result.AlreadySubmitted -> {
            sendReply(Strings.AlreadyApplied)
        }
        is SubmitApplicationUseCase.Result.Expired -> {
            sendReply(Strings.CourseExpired)
        }
        is SubmitApplicationUseCase.Result.NotRegistered -> {
            startRegistration(setState)
        }
        is SubmitApplicationUseCase.Result.NoSuchCourse -> {
            sendReply(Strings.CourseNotFound)
        }
        is SubmitApplicationUseCase.Result.AdditionalInfoRequired -> {
            sendReply(result.question)
            setState(state)
        }
    }
    bot.editMessageReplyMarkup(
        chatId = ChatId.fromId(chatId),
        messageId = state.messageId,
        replyMarkup = detailsReplyMarkup(state.courseId, isApplicationSubmitted)
    )
}

context(Permissions)
fun CallbackQueryHandlerEnvironment.revoke(
    courseId: Long,
    revokeApplication: RevokeApplicationUseCase,
    isApplicationSubmitted: IsApplicationSubmittedUseCase
) {
    when (revokeApplication(courseId)) {
        is RevokeApplicationUseCase.Result.OK -> {}
        RevokeApplicationUseCase.Result.NotRegistered -> {
            sendReply(Strings.NotRegistered)
        }
    }
    bot.editMessageReplyMarkup(
        chatId = ChatId.fromId(chatId),
        messageId = callbackQuery.message?.messageId,
        replyMarkup = detailsReplyMarkup(courseId, isApplicationSubmitted)
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

context(Permissions)
private fun detailsReplyMarkup(
    courseId: Long,
    isApplicationSubmitted: IsApplicationSubmittedUseCase
): ReplyMarkup {
    val button = when (val isSubmitted = isApplicationSubmitted(courseId)) {
        is IsApplicationSubmittedUseCase.Result.OK -> {
            if (isSubmitted.value) {
                InlineKeyboardButton.CallbackData(Strings.RevokeApplication, "revoke $courseId")
            } else {
                InlineKeyboardButton.CallbackData(Strings.SubmitApplication, "apply $courseId")
            }
        }
        IsApplicationSubmittedUseCase.Result.NotRegistered -> {
            InlineKeyboardButton.CallbackData(Strings.NotRegistered, "apply $courseId")
        }
    }
    return InlineKeyboardMarkup.createSingleButton(button)
}
