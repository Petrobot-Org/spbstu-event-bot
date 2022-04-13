package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.RegisterClientUseCase
import ru.spbstu.eventbot.telegram.ChatState
import ru.spbstu.eventbot.telegram.ClientRegistrationRequest
import ru.spbstu.eventbot.telegram.Strings
import ru.spbstu.eventbot.telegram.sendReply

class ClientRegistrationFlow(private val registerClient: RegisterClientUseCase) {
    context(TextHandlerEnvironment)
    fun start(
        setState: (ChatState) -> Unit
    ) {
        sendReply(Strings.RequestClientName)
        setState(ChatState.ClientRegistration(ClientRegistrationRequest.Name))
    }

    context(Permissions, TextHandlerEnvironment)
    fun handle(
        state: ChatState.ClientRegistration,
        setState: (ChatState) -> Unit
    ) {
        when (state.request) {
            ClientRegistrationRequest.Name -> handleName(state, setState)
            ClientRegistrationRequest.Email -> handleEmail(state, setState)
            ClientRegistrationRequest.UserId -> handleUserId(state, setState)
            ClientRegistrationRequest.Confirm -> handleConfirmation(state, setState)
        }
    }

    context(TextHandlerEnvironment)
    private fun handleName(state: ChatState.ClientRegistration, setState: (ChatState) -> Unit) {
        if (!registerClient.isNameValid(text)) {
            sendReply(Strings.InvalidClientName)
            return
        }
        setState(state.copy(name = text, request = ClientRegistrationRequest.Email))
        sendReply(Strings.RequestClientEmail)
    }

    context(TextHandlerEnvironment)
    private fun handleEmail(state: ChatState.ClientRegistration, setState: (ChatState) -> Unit) {
        if (!registerClient.isEmailValid(text)) {
            sendReply(Strings.InvalidEmail)
            return
        }
        setState(state.copy(email = text, request = ClientRegistrationRequest.UserId))
        sendReply(Strings.RequestClientUserId)
    }

    context(TextHandlerEnvironment)
    private fun handleUserId(state: ChatState.ClientRegistration, setState: (ChatState) -> Unit) {
        val userId = if (text in Strings.NegativeAnswers) {
            null
        } else {
            text.toLongOrNull() ?: run {
                sendReply(Strings.InvalidClientUserId)
                return
            }
        }
        setState(state.copy(userId = userId, request = ClientRegistrationRequest.Confirm))
        sendReply(
            Strings.clientRegistrationConfirmation(
                name = state.name!!,
                email = state.email!!,
                userId = userId
            )
        )
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleConfirmation(state: ChatState.ClientRegistration, setState: (ChatState) -> Unit) {
        when (text.lowercase()) {
            in Strings.PositiveAnswers -> {
                val result = registerClient(state.name!!, state.email!!, state.userId)
                when (result) {
                    RegisterClientUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.ClientRegisteredSuccessfully)
                    }
                    RegisterClientUseCase.Result.InvalidArguments -> {
                        sendReply(Strings.RegistrationErrorRetry)
                        start(setState)
                    }
                    RegisterClientUseCase.Result.Unauthorized -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.UnauthorizedError)
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
}
