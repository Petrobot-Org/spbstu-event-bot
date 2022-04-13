package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.entities.AdditionalQuestion
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.CreateNewCourseUseCase
import ru.spbstu.eventbot.domain.usecases.GetMyClientsUseCase
import ru.spbstu.eventbot.telegram.ChatState
import ru.spbstu.eventbot.telegram.NewCourseCreationRequest
import ru.spbstu.eventbot.telegram.Strings
import ru.spbstu.eventbot.telegram.sendReply
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CourseCreationFlow(
    private val getMyClients: GetMyClientsUseCase,
    private val createNewCourse: CreateNewCourseUseCase,
    private val zone: ZoneId
) {
    context(Permissions, TextHandlerEnvironment)
    fun start() {
        when (val result = getMyClients()) {
            is GetMyClientsUseCase.Result.OK -> {
                val buttons = result.clients.map {
                    listOf(InlineKeyboardButton.CallbackData(it.name.value, "newcourse ${it.id}"))
                }
                sendReply(
                    text = Strings.SelectClient,
                    replyMarkup = InlineKeyboardMarkup.create(buttons)
                )
            }
            GetMyClientsUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
        }
    }

    context(Permissions, CallbackQueryHandlerEnvironment)
    fun onClientSelected(clientId: Long, setState: (ChatState) -> Unit) {
        setState(ChatState.NewCourseCreation(NewCourseCreationRequest.Title, clientId))
        sendReply(Strings.RequestTitle)
    }

    context(Permissions, TextHandlerEnvironment)
    fun handle(state: ChatState.NewCourseCreation, setState: (ChatState) -> Unit) {
        val newState = when (state.request) {
            NewCourseCreationRequest.Title -> handleTitle(state)
            NewCourseCreationRequest.Description -> handleDescription(state)
            NewCourseCreationRequest.AdditionalQuestion -> handleAdditionalQuestion(state)
            NewCourseCreationRequest.ExpiryDate -> handleExpiryDate(state)
            NewCourseCreationRequest.Confirm -> {
                handleConfirmation(state, setState)
                return
            }
        }
        val request = requestInfo(newState)
        setState(newState.copy(request = request))
    }

    context(TextHandlerEnvironment)
    private fun handleTitle(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        return state.copy(title = text)
    }

    context(TextHandlerEnvironment)
    private fun handleDescription(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        return state.copy(description = text)
    }

    context(TextHandlerEnvironment)
    private fun handleAdditionalQuestion(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val additionalQuestion = AdditionalQuestion(if (text in Strings.NegativeAnswers) null else text)
        return state.copy(additionalQuestion = additionalQuestion)
    }

    context(TextHandlerEnvironment)
    private fun handleExpiryDate(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val formatter = DateTimeFormatter
            .ofPattern("dd.MM.uuuu HH:mm")
            .withZone(zone)
        val date = try {
            formatter.parse(text, Instant::from)
        } catch (e: DateTimeParseException) {
            sendReply(Strings.InvalidDate)
            return state
        }
        return state.copy(expiryDate = date)
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleConfirmation(state: ChatState.NewCourseCreation, setState: (ChatState) -> Unit) {
        when (text.lowercase()) {
            in Strings.PositiveAnswers -> {
                val result = createNewCourse(
                    state.clientId,
                    state.title!!,
                    state.description!!,
                    state.additionalQuestion!!,
                    state.expiryDate!!
                )
                when (result) {
                    CreateNewCourseUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.CreatedNewCourseSuccessfully)
                    }
                    CreateNewCourseUseCase.Result.InvalidArguments -> {
                        sendReply(Strings.CreationErrorRetry)
                        start()
                    }
                    CreateNewCourseUseCase.Result.Unauthorized -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.UnauthorizedError)
                    }
                    CreateNewCourseUseCase.Result.NoSuchClient -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.NoSuchClient)
                    }
                }
            }
            in Strings.NegativeAnswers -> {
                sendReply(Strings.CreationErrorRetry)
                start()
            }
            else -> {
                sendReply(Strings.RequestYesNo)
            }
        }
    }

    context(TextHandlerEnvironment)
    private fun requestInfo(state: ChatState.NewCourseCreation): NewCourseCreationRequest {
        return when {
            state.title == null -> {
                sendReply(Strings.RequestTitle)
                NewCourseCreationRequest.Title
            }
            state.description == null -> {
                sendReply(Strings.RequestDescription)
                NewCourseCreationRequest.Description
            }
            state.additionalQuestion == null -> {
                sendReply(Strings.RequestAdditionalQuestion)
                NewCourseCreationRequest.AdditionalQuestion
            }
            state.expiryDate == null -> {
                sendReply(Strings.RequestExpiryDate)
                NewCourseCreationRequest.ExpiryDate
            }
            else -> {
                sendReply(
                    Strings.newCourseCreationConfirmation(
                        title = state.title,
                        description = state.description,
                        additionalQuestion = state.additionalQuestion.value,
                        expiryDate = state.expiryDate
                    )
                )
                NewCourseCreationRequest.Confirm
            }
        }
    }
}
