package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.RegisterStudentUseCase
import ru.spbstu.eventbot.telegram.ChatState
import ru.spbstu.eventbot.telegram.RegistrationRequest
import ru.spbstu.eventbot.telegram.Strings
import ru.spbstu.eventbot.telegram.sendReply

class RegistrationFlow(
    private val registerStudent: RegisterStudentUseCase
) {
    context(TextHandlerEnvironment)
    fun start(setState: (ChatState) -> Unit) {
        setState(ChatState.Registration(RegistrationRequest.FullName))
        sendReply(Strings.RequestName)
    }

    context(Permissions, TextHandlerEnvironment)
    fun handle(state: ChatState.Registration, setState: (ChatState) -> Unit) {
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
                handleConfirmation(state, setState)
                return
            }
        }
        val request = requestInfo(newState)
        setState(newState.copy(request = request))
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleConfirmation(
        state: ChatState.Registration,
        setState: (ChatState) -> Unit
    ) {
        when (text.lowercase()) {
            in Strings.PositiveAnswers -> {
                val result = registerStudent(state.fullName!!, state.email!!, state.group!!)
                when (result) {
                    RegisterStudentUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.RegisteredSuccessfully)
                    }
                    RegisterStudentUseCase.Result.InvalidArguments -> {
                        sendReply(Strings.RegistrationErrorRetry)
                        start(setState)
                    }
                }
            }
            in Strings.NegativeAnswers -> {
                sendReply(Strings.RegistrationRetry)
                start(setState)
            }
            else -> {
                sendReply(Strings.RequestYesNo)
            }
        }
    }

    context(TextHandlerEnvironment)
    private fun requestInfo(state: ChatState.Registration): RegistrationRequest {
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
}
