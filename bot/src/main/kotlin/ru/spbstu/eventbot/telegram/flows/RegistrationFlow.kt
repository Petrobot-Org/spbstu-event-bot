package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
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

    context(CallbackQueryHandlerEnvironment)
    fun start(setState: (ChatState) -> Unit) {
        setState(ChatState.Registration(RegistrationRequest.FullName))
        sendReply(Strings.RequestName)
    }

    context(Permissions, TextHandlerEnvironment)
    fun handle(state: ChatState.Registration, setState: (ChatState) -> Unit) {
        val newState = when (state.request) {
            RegistrationRequest.FullName -> {
                val fullName = FullName.valueOf(text) ?: return sendReply(Strings.InvalidName)
                state.copy(fullName = fullName)
            }
            RegistrationRequest.Email -> {
                val email = Email.valueOf(text) ?: return sendReply(Strings.InvalidEmail)
                state.copy(email = email)
            }
            RegistrationRequest.Group -> {
                val group = Group.valueOf(text) ?: return sendReply(Strings.InvalidGroup)
                state.copy(group = group)
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
                when (registerStudent(state.fullName!!, state.email!!, state.group!!)) {
                    RegisterStudentUseCase.Result.Error -> {
                        start(setState)
                        sendReply(Strings.ErrorRetry)
                    }
                    RegisterStudentUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.RegisteredSuccessfully)
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
