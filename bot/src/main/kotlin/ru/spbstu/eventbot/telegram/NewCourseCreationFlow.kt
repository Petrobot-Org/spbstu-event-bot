package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.usecases.CreateNewCourseUseCase
import java.time.Instant
import java.time.format.DateTimeFormatter

fun TextHandlerEnvironment.startNewCourseCreation(
    setState: (ChatState) -> Unit
) {
    val request = requestInfo(null)
    setState(ChatState.NewCourseCreation(request))
}

fun TextHandlerEnvironment.handleNewCourseCreation(
    state: ChatState.NewCourseCreation,
    setState: (ChatState) -> Unit,
    createNewCourse: CreateNewCourseUseCase
) {
    val newState = when (state.request) {
        NewCourseCreationRequest.Title -> {
            state.copy(title = text)
        }
        NewCourseCreationRequest.Description -> {
            state.copy(description = text)
        }
        NewCourseCreationRequest.AdditionalQuestion -> {
            state.copy(additionalQuestion = text)
        }
        NewCourseCreationRequest.ExpiryDate -> {
            val date = DateTimeFormatter.ofPattern(text).parse(text)
            state.copy(expiryDate = Instant.parse(date.toString())) // /?
        }
        NewCourseCreationRequest.Confirm -> {
            handleConfirmation(state, setState, createNewCourse)
            return
        }
    }
    val request = requestInfo(newState)
    setState(newState.copy(request = request))
}

private fun TextHandlerEnvironment.handleConfirmation(
    state: ChatState.NewCourseCreation,
    setState: (ChatState) -> Unit,
    createNewCourse: CreateNewCourseUseCase
) {
    when (text.lowercase()) {
        in Strings.PositiveAnswers -> { // //добавить выбор заказчика, если их несколько
            val result = createNewCourse(message.chat.id, state.title!!, state.description!!, state.additionalQuestion!!, state.expiryDate!!)
            when (result) {
                CreateNewCourseUseCase.Result.OK -> {
                    setState(ChatState.Empty)
                    sendReply(Strings.CreatedNewCourseSuccessfully)
                }
                CreateNewCourseUseCase.Result.InvalidArguments -> {
                    sendReply(Strings.CreationErrorRetry)
                    startNewCourseCreation(setState)
                }
            }
        }
        in Strings.NegativeAnswers -> {
            sendReply(Strings.CreationErrorRetry)
            startNewCourseCreation(setState)
        }
        else -> {
            sendReply(Strings.RequestYesNo)
        }
    }
}

private fun TextHandlerEnvironment.requestInfo(
    state: ChatState.NewCourseCreation?
): NewCourseCreationRequest {
    return when {
        state?.title == null -> {
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
                    additionalQuestion = state.additionalQuestion,
                    expiryDate = state.expiryDate,
                )
            )
            NewCourseCreationRequest.Confirm
        }
    }
}
