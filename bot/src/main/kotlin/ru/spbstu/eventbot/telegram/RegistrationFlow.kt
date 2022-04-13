package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.RegisterStudentUseCase

fun CallbackQueryHandlerEnvironment.startRegistration(
    setNewState: (ChatState) -> Unit
) {
    setNewState(ChatState.Registration(RegistrationRequest.FullName))
    sendReply(Strings.RequestName)
}

fun TextHandlerEnvironment.startRegistration(
    setNewState: (ChatState) -> Unit
) {
    setNewState(ChatState.Registration(RegistrationRequest.FullName))
    sendReply(Strings.RequestName)
}

context(Permissions)
fun TextHandlerEnvironment.handleRegistration(
    state: ChatState.Registration,
    setNewState: (ChatState) -> Unit,
    registerStudent: RegisterStudentUseCase
) {
    val newState = when (state.request) {
        RegistrationRequest.FullName -> {
            if (!registerStudent.isFullNameValid(text)) {
                sendReply(Strings.InvalidName)
                return
            }
            state.copy(fullName = text)
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

context(Permissions)
private fun TextHandlerEnvironment.handleConfirmation(
    registerStudent: RegisterStudentUseCase,
    state: ChatState.Registration,
    setNewState: (ChatState) -> Unit
) {
    when (text.lowercase()) {
        in Strings.PositiveAnswers -> {
            val result = registerStudent(state.fullName!!, state.email!!, state.group!!)
            when (result) {
                RegisterStudentUseCase.Result.OK -> {
                    setNewState(ChatState.Empty)
                    sendReply(Strings.RegisteredSuccessfully)
                }
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
        state.fullName == null -> {
            sendReply(Strings.RequestName)
            RegistrationRequest.FullName
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
                    name = state.fullName,
                    email = state.email,
                    group = state.group
                )
            )
            RegistrationRequest.Confirm
        }
    }
}
