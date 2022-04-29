package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ParseMode
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.RegisterStudentUseCase
import ru.spbstu.eventbot.telegram.*

class RegistrationFlow(
    private val registerStudent: RegisterStudentUseCase
) {
    context(TextHandlerEnvironment, StateEnvironment<ChatState>)
    fun start() {
        setState(ChatState.Registration(RegistrationRequest.FullName))
        sendReply(Strings.RequestName)
    }

    context(CallbackQueryHandlerEnvironment, StateEnvironment<ChatState>)
    fun start() {
        setState(ChatState.Registration(RegistrationRequest.FullName))
        sendReply(Strings.RequestName)
    }

    context(Permissions, StateEnvironment<ChatState.Registration>, TextHandlerEnvironment)
    fun handle() {
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
                handleConfirmation()
                return
            }
        }
        val request = requestInfo(newState)
        setState(newState.copy(request = request))
    }

    context(Permissions, StateEnvironment<ChatState.Registration>, TextHandlerEnvironment)
    private fun handleConfirmation() {
        when (text.lowercase()) {
            in Strings.PositiveAnswers -> {
                when (registerStudent(state.fullName!!, state.email!!, state.group!!)) {
                    RegisterStudentUseCase.Result.Error -> {
                        start()
                        sendReply(Strings.ErrorRetry)
                    }
                    RegisterStudentUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(text = Strings.RegisteredSuccessfully, parseMode = ParseMode.MARKDOWN)
                    }
                }
            }
            in Strings.NegativeAnswers -> {
                sendReply(Strings.RegistrationRetry)
                start()
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
