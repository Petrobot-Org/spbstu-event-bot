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
import ru.spbstu.eventbot.domain.usecases.*
import ru.spbstu.eventbot.telegram.ChatState
import ru.spbstu.eventbot.telegram.Strings
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
    context(TextHandlerEnvironment)
    fun display() {
        val courses = getAvailableCourses()
        val buttons = courses.map {
            listOf(InlineKeyboardButton.CallbackData(it.title.value, "details ${it.id}"))
        }
        sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
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
                sendReply(result.question)
                setState(ChatState.AdditionalInfoRequested(courseId, callbackQuery.message?.messageId))
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
            is RevokeApplicationUseCase.Result.OK -> {}
            RevokeApplicationUseCase.Result.NotRegistered -> {
                sendReply(Strings.NotRegistered)
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
                    listOf(InlineKeyboardButton.CallbackData(it.title.value, "applicants ${it.id}"))
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
}
