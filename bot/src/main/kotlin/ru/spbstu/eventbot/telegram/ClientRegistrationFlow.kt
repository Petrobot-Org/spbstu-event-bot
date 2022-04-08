package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.RegisterClientUseCase

fun TextHandlerEnvironment.startClientRegistration(
    setState: (ChatState) -> Unit
) {
    sendReply(Strings.RequestClientName)
    setState(ChatState.ClientRegistration(ClientRegistrationRequest.Name))
}

context(Permissions)
fun TextHandlerEnvironment.handleClientRegistration(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit,
    registerClient: RegisterClientUseCase
) {
    when (state.request) {
        ClientRegistrationRequest.Name -> handleName(state, setState, registerClient)
        ClientRegistrationRequest.Email -> handleEmail(state, setState, registerClient)
        ClientRegistrationRequest.UserId -> handleUserId(state, setState)
        ClientRegistrationRequest.Confirm -> handleConfirmation(state, setState, registerClient)
    }
}

private fun TextHandlerEnvironment.handleName(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit,
    registerClient: RegisterClientUseCase
) {
    if (!registerClient.isNameValid(text)) {
        sendReply(Strings.InvalidClientName)
        return
    }
    setState(state.copy(name = text, request = ClientRegistrationRequest.Email))
    sendReply(Strings.RequestClientEmail)
}

private fun TextHandlerEnvironment.handleEmail(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit,
    registerClient: RegisterClientUseCase
) {
    if (!registerClient.isEmailValid(text)) {
        sendReply(Strings.InvalidEmail)
        return
    }
    setState(state.copy(email = text, request = ClientRegistrationRequest.UserId))
    sendReply(Strings.RequestClientUserId)
}

private fun TextHandlerEnvironment.handleUserId(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit
) {
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
            userId = state.userId
        )
    )
}

context(Permissions)
private fun TextHandlerEnvironment.handleConfirmation(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit,
    registerClient: RegisterClientUseCase
) {
    when (text.lowercase()) {
        in Strings.PositiveAnswers -> {
            val result = registerClient(state.name!!, state.email!!, state.userId)
            when (result) {
                RegisterClientUseCase.Result.OK -> {
                    setState(ChatState.Empty)
                    sendReply(Strings.ClientRegisteredSuccessfully)
                }
                RegisterClientUseCase.Result.InvalidArguments -> {
                    sendReply(Strings.ClientRegistrationErrorRetry)
                    startClientRegistration(setState)
                }
                RegisterClientUseCase.Result.Unauthorized -> {
                    setState(ChatState.Empty)
                    sendReply(Strings.UnauthorizedError)
                }
            }
        }
        in Strings.NegativeAnswers -> {
            sendReply(Strings.ClientRegistrationRetry)
            startClientRegistration(setState)
        }
        else -> {
            sendReply(Strings.RequestYesNo)
        }
    }
}
