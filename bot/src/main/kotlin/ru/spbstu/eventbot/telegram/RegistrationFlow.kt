package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.usecases.RegisterStudentUseCase

fun TextHandlerEnvironment.startRegistration(
    setNewState: (ChatState) -> Unit
) {
    setNewState(ChatState.Registration(RegistrationRequest.Name))
    sendReply(Strings.RequestName)
}

fun TextHandlerEnvironment.handleRegistration(
    state: ChatState.Registration,
    setNewState: (ChatState) -> Unit,
    registerStudent: RegisterStudentUseCase
) {
    val newState = when (state.request) {
        RegistrationRequest.Name -> {
            if (!registerStudent.isNameValid(text)) {
                sendReply(Strings.InvalidName)
                return
            }
            state.copy(name = text)
        }
        RegistrationRequest.Email -> {
            if (!registerStudent.isEmailValid(text)) {
                sendReply(Strings.InvalidEmail)
                return
            }
            state.copy(email = text)
        }
        RegistrationRequest.Group -> {
            if (!registerStudent.isGroupValid(text)) {
                sendReply(Strings.InvalidGroup)
                return
            }
            state.copy(group = text)
        }
        RegistrationRequest.Confirm -> {
            handleConfirmation(registerStudent, state, setNewState)
            return
        }
    }
    val request = requestInfo(newState)
    setNewState(newState.copy(request = request))
}

private fun TextHandlerEnvironment.handleConfirmation(
    registerStudent: RegisterStudentUseCase,
    state: ChatState.Registration,
    setNewState: (ChatState) -> Unit
) {
    when (text.lowercase()) {
        in Strings.PositiveAnswers -> {
            val result = registerStudent(message.chat.id, state.name!!, state.email!!, state.group!!)
            when (result) {
                RegisterStudentUseCase.Result.OK -> {
                    setNewState(ChatState.Empty)
                    sendReply(Strings.RegisteredSuccessfully)
                }
                RegisterStudentUseCase.Result.Error,
                RegisterStudentUseCase.Result.InvalidArguments -> {
                    sendReply(Strings.RegistrationErrorRetry)
                    startRegistration(setNewState)
                }
            }
        }
        in Strings.NegativeAnswers -> {
            sendReply(Strings.RegistrationRetry)
            startRegistration(setNewState)
        }
        else -> {
            sendReply(Strings.RequestYesNo)
        }
    }
}

private fun TextHandlerEnvironment.requestInfo(
    state: ChatState.Registration
): RegistrationRequest {
    return when {
        state.name == null -> {
            sendReply(Strings.RequestName)
            RegistrationRequest.Name
        }
        state.email == null -> {
            sendReply(Strings.RequestEmail)
            RegistrationRequest.Email
        }
        state.group == null -> {
            sendReply(Strings.RequestGroup)
            RegistrationRequest.Group
        }
        else -> {
            sendReply(
                Strings.registrationConfirmation(
                    name = state.name,
                    email = state.email,
                    group = state.group
                )
            )
            RegistrationRequest.Confirm
        }
    }
}
