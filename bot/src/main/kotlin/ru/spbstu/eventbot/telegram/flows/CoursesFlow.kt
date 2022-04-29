package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.permissions.Permissions.App.chatId
import ru.spbstu.eventbot.domain.usecases.*
import ru.spbstu.eventbot.telegram.ChatState
import ru.spbstu.eventbot.telegram.Strings
import ru.spbstu.eventbot.telegram.Strings.RequestAdditionalInfo
import ru.spbstu.eventbot.telegram.Strings.RequestAnswer
import ru.spbstu.eventbot.telegram.sendReply

class CoursesFlow(
    private val getAvailableCourses: GetAvailableCoursesUseCase,
    private val isApplicationSubmitted: IsApplicationSubmittedUseCase,
    private val getCourseById: GetCourseByIdUseCase,
    private val submitApplication: SubmitApplicationUseCase,
    private val getApplicants: GetApplicationsByCourseIdUseCase,
    private val revokeApplication: RevokeApplicationUseCase,
    private val getClientCourses: GetClientCoursesUseCase,
    private val registrationFlow: RegistrationFlow
) {
    context(Permissions, TextHandlerEnvironment)
    fun display() {
        when (val result = getAvailableCourses()) {
            is GetAvailableCoursesUseCase.Result.OK -> {
                val buttons = result.courses.map {
                    listOf(InlineKeyboardButton.CallbackData(it.title.value, "details ${it.id.value}"))
                }
                sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
            }
            GetAvailableCoursesUseCase.Result.NotRegistered -> {
                sendReply(Strings.NotRegistered)
            }
        }
    }

    context(Permissions, CallbackQueryHandlerEnvironment)
    fun details(courseId: CourseId) {
        val course = getCourseById(courseId) ?: return sendReply(Strings.NoSuchCourse)
        sendReply(
            text = Strings.courseDetails(course),
            parseMode = ParseMode.MARKDOWN,
            replyMarkup = detailsReplyMarkup(courseId)
        )
    }

    context(Permissions, CallbackQueryHandlerEnvironment)
    fun apply(
        courseId: CourseId,
        setState: (ChatState) -> Unit
    ) {
        when (val result = submitApplication(courseId)) {
            is SubmitApplicationUseCase.Result.OK -> {}
            is SubmitApplicationUseCase.Result.AlreadySubmitted -> {
                sendReply(Strings.AlreadyApplied)
            }
            is SubmitApplicationUseCase.Result.Expired -> {
                sendReply(Strings.CourseExpired)
            }
            is SubmitApplicationUseCase.Result.NotRegistered -> {
                registrationFlow.start(setState)
            }
            is SubmitApplicationUseCase.Result.NoSuchCourse -> {
                sendReply(Strings.CourseNotFound)
            }
            is SubmitApplicationUseCase.Result.AdditionalInfoRequired -> {
                sendReply(text=RequestAdditionalInfo + result.question + RequestAnswer)
                setState(ChatState.AdditionalInfoRequested(courseId, callbackQuery.message?.messageId))
            }
            SubmitApplicationUseCase.Result.Error -> {
                sendReply(Strings.SubmitError)
            }
        }
        bot.editMessageReplyMarkup(
            chatId = ChatId.fromId(chatId),
            messageId = callbackQuery.message?.messageId,
            replyMarkup = detailsReplyMarkup(courseId)
        )
    }

    // TODO: Deal with code duplication
    context(Permissions, TextHandlerEnvironment)
    fun handleAdditionalInfo(
        state: ChatState.AdditionalInfoRequested,
        setState: (ChatState) -> Unit
    ) {
        setState(ChatState.Empty)
        when (val result = submitApplication(state.courseId, text)) {
            is SubmitApplicationUseCase.Result.OK -> {}
            is SubmitApplicationUseCase.Result.AlreadySubmitted -> {
                sendReply(Strings.AlreadyApplied)
            }
            is SubmitApplicationUseCase.Result.Expired -> {
                sendReply(Strings.CourseExpired)
            }
            is SubmitApplicationUseCase.Result.NotRegistered -> {
                registrationFlow.start(setState)
            }
            is SubmitApplicationUseCase.Result.NoSuchCourse -> {
                sendReply(Strings.CourseNotFound)
            }
            is SubmitApplicationUseCase.Result.AdditionalInfoRequired -> {
                sendReply(result.question)
                setState(state)
            }
            SubmitApplicationUseCase.Result.Error -> {
                sendReply(Strings.SubmitError)
            }
        }
        bot.editMessageReplyMarkup(
            chatId = ChatId.fromId(chatId),
            messageId = state.messageId,
            replyMarkup = detailsReplyMarkup(state.courseId)
        )
    }

    context(Permissions, CallbackQueryHandlerEnvironment)
    fun revoke(courseId: CourseId) {
        when (revokeApplication(courseId)) {
            RevokeApplicationUseCase.Result.OK -> {}
            RevokeApplicationUseCase.Result.NotRegistered -> {
                sendReply(Strings.NotRegistered)
            }
            RevokeApplicationUseCase.Result.Error -> {
                sendReply(Strings.RevokeError)
            }
        }
        bot.editMessageReplyMarkup(
            chatId = ChatId.fromId(chatId),
            messageId = callbackQuery.message?.messageId,
            replyMarkup = detailsReplyMarkup(courseId)
        )
    }

    context(Permissions, TextHandlerEnvironment)
    fun displayApplicants() {
        when (val result = getClientCourses()) {
            is GetClientCoursesUseCase.Result.OK -> {
                val buttons = result.courses.map {
                    listOf(InlineKeyboardButton.CallbackData(it.title.value, "applicants ${it.id.value}"))
                }
                sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
            }
            GetClientCoursesUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
        }
    }

    context(Permissions, CallbackQueryHandlerEnvironment)
    fun applicantsInfo(courseId: CourseId) {
        when (val result = getApplicants(courseId)) {
            is GetApplicationsByCourseIdUseCase.Result.OK -> {
                if (result.applications.isEmpty()) {
                    sendReply(Strings.NoApplicants)
                    return
                }
                sendReply(
                    text = Strings.applicantsInfo(result.applications),
                    parseMode = ParseMode.MARKDOWN
                )
            }
            GetApplicationsByCourseIdUseCase.Result.NoSuchCourse -> sendReply(Strings.NoSuchCourse)
            GetApplicationsByCourseIdUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
        }
    }

    context(Permissions)
    private fun detailsReplyMarkup(courseId: CourseId): ReplyMarkup {
        val button = when (val isSubmitted = isApplicationSubmitted(courseId)) {
            is IsApplicationSubmittedUseCase.Result.OK -> {
                if (isSubmitted.value) {
                    InlineKeyboardButton.CallbackData(Strings.RevokeApplication, "revoke ${courseId.value}")
                } else {
                    InlineKeyboardButton.CallbackData(Strings.SubmitApplication, "apply ${courseId.value}")
                }
            }
            IsApplicationSubmittedUseCase.Result.NotRegistered -> {
                InlineKeyboardButton.CallbackData(Strings.NotRegistered, "apply ${courseId.value}")
            }
        }
        return InlineKeyboardMarkup.createSingleButton(button)
    }
}
