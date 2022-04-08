package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.entities.AdditionalQuestion
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.CreateNewCourseUseCase
import ru.spbstu.eventbot.domain.usecases.GetMyClientsUseCase
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

context(Permissions)
fun TextHandlerEnvironment.selectClientForCourseCreation(
    getMyClients: GetMyClientsUseCase
) {
    when (val result = getMyClients()) {
        is GetMyClientsUseCase.Result.OK -> {
            val buttons = result.clients.map {
                InlineKeyboardButton.CallbackData(it.name, "newcourse ${it.id}")
            }
            sendReply(
                text = Strings.SelectClient,
                replyMarkup = InlineKeyboardMarkup.create(buttons)
            )
        }
        GetMyClientsUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
    }
}

context(Permissions)
fun CallbackQueryHandlerEnvironment.startCourseCreation(
    clientId: Long,
    setState: (ChatState) -> Unit
) {
    setState(ChatState.NewCourseCreation(NewCourseCreationRequest.Title, clientId))
    sendReply(Strings.RequestTitle)
}

context(Permissions)
fun TextHandlerEnvironment.handleNewCourseCreation(
    state: ChatState.NewCourseCreation,
    setState: (ChatState) -> Unit,
    createNewCourse: CreateNewCourseUseCase,
    getMyClients: GetMyClientsUseCase,
    zone: ZoneId
) {
    val newState = when (state.request) {
        NewCourseCreationRequest.Title -> handleTitle(state)
        NewCourseCreationRequest.Description -> handleDescription(state)
        NewCourseCreationRequest.AdditionalQuestion -> handleAdditionalQuestion(state)
        NewCourseCreationRequest.ExpiryDate -> handleExpiryDate(state, zone)
        NewCourseCreationRequest.Confirm -> {
            handleConfirmation(state, setState, createNewCourse, getMyClients)
            return
        }
    }
    val request = requestInfo(newState)
    setState(newState.copy(request = request))
}

private fun TextHandlerEnvironment.handleTitle(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
    return state.copy(title = text)
}

private fun TextHandlerEnvironment.handleDescription(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
    return state.copy(description = text)
}

private fun TextHandlerEnvironment.handleAdditionalQuestion(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
    val additionalQuestion = AdditionalQuestion(if (text in Strings.NegativeAnswers) null else text)
    return state.copy(additionalQuestion = additionalQuestion)
}

private fun TextHandlerEnvironment.handleExpiryDate(
    state: ChatState.NewCourseCreation,
    zone: ZoneId
): ChatState.NewCourseCreation {
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

context(Permissions)
private fun TextHandlerEnvironment.handleConfirmation(
    state: ChatState.NewCourseCreation,
    setState: (ChatState) -> Unit,
    createNewCourse: CreateNewCourseUseCase,
    getMyClients: GetMyClientsUseCase
) {
    when (text.lowercase()) {
        in Strings.PositiveAnswers -> {
            val result = createNewCourse(state.clientId, state.title!!, state.description!!, state.additionalQuestion!!, state.expiryDate!!)
            when (result) {
                CreateNewCourseUseCase.Result.OK -> {
                    setState(ChatState.Empty)
                    sendReply(Strings.CreatedNewCourseSuccessfully)
                }
                CreateNewCourseUseCase.Result.InvalidArguments -> {
                    sendReply(Strings.CreationErrorRetry)
                    selectClientForCourseCreation(getMyClients)
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
            selectClientForCourseCreation(getMyClients)
        }
        else -> {
            sendReply(Strings.RequestYesNo)
        }
    }
}

private fun TextHandlerEnvironment.requestInfo(
    state: ChatState.NewCourseCreation
): NewCourseCreationRequest {
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
