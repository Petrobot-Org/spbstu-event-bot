package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.entities.ClientName
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.RegisterClientUseCase
import ru.spbstu.eventbot.telegram.*

class ClientRegistrationFlow(private val registerClient: RegisterClientUseCase) {
    context(TextHandlerEnvironment, StateEnvironment<ChatState>)
    fun start() {
        sendReply(Strings.RequestClientName)
        setState(ChatState.ClientRegistration(ClientRegistrationRequest.Name))
    }

    context(Permissions, StateEnvironment<ChatState.ClientRegistration>, TextHandlerEnvironment)
    fun handle() {
        when (state.request) {
            ClientRegistrationRequest.Name -> handleName()
            ClientRegistrationRequest.Email -> handleEmail()
            ClientRegistrationRequest.UserId -> handleUserId()
            ClientRegistrationRequest.Confirm -> handleConfirmation()
        }
    }

    context(StateEnvironment<ChatState.ClientRegistration>, TextHandlerEnvironment)
    private fun handleName() {
        val name = ClientName.valueOf(text) ?: return sendReply(Strings.InvalidClientName)
        setState(state.copy(name = name, request = ClientRegistrationRequest.Email))
        sendReply(Strings.RequestClientEmail)
    }

    context(StateEnvironment<ChatState.ClientRegistration>, TextHandlerEnvironment)
    private fun handleEmail() {
        val email = Email.valueOf(text) ?: return sendReply(Strings.InvalidEmail)
        setState(state.copy(email = email, request = ClientRegistrationRequest.UserId))
        sendReply(Strings.RequestClientUserId)
    }

    context(Permissions, StateEnvironment<ChatState.ClientRegistration>, TextHandlerEnvironment)
    private fun handleUserId() {
        val userId = if (text in Strings.NegativeAnswers) {
            userId
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

    context(Permissions, StateEnvironment<ChatState.ClientRegistration>, TextHandlerEnvironment)
    private fun handleConfirmation() {
        when (text.lowercase()) {
            in Strings.PositiveAnswers -> {
                when (registerClient(state.name!!, state.email!!, state.userId!!)) {
                    RegisterClientUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.ClientRegisteredSuccessfully)
                    }
                    RegisterClientUseCase.Result.Unauthorized -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.UnauthorizedError)
                    }
                    RegisterClientUseCase.Result.Error -> {
                        sendReply(Strings.ErrorRetry)
                        start()
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
}
