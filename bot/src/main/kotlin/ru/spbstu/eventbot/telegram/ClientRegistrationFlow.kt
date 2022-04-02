package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.usecases.RegisterClientUseCase

fun TextHandlerEnvironment.startClientRegistration(
    setState: (ChatState) -> Unit
) {
    val request = requestInfo(null)
    setState(ChatState.ClientRegistration(request))
}

fun TextHandlerEnvironment.handleClientRegistration(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit,
    registerClient: RegisterClientUseCase
) {
    val newState = when (state.request) {
        ClientRegistrationRequest.Name -> {
            if (!registerClient.isNameValid(text)) {
                sendReply(Strings.InvalidClientName)
                return
            }
            state.copy(name = text)
        }
        ClientRegistrationRequest.Email -> {
            if (!registerClient.isEmailValid(text)) {
                sendReply(Strings.InvalidEmail)
                return
            }
            state.copy(email = text)
        }
        ClientRegistrationRequest.Confirm -> {
            handleConfirmation(state, setState, registerClient)
            return
        }
    }
    val request = requestInfo(newState)
    setState(newState.copy(request = request))
}

private fun TextHandlerEnvironment.handleConfirmation(
    state: ChatState.ClientRegistration,
    setState: (ChatState) -> Unit,
    registerClient: RegisterClientUseCase
) {
    when (text.lowercase()) {
        in Strings.PositiveAnswers -> {
            val result = registerClient(state.name!!, state.email!!)
            when (result) {
                RegisterClientUseCase.Result.OK -> {
                    setState(ChatState.Empty)
                    sendReply(Strings.ClientRegisteredSuccessfully)
                }
                RegisterClientUseCase.Result.InvalidArguments -> {
                    sendReply(Strings.ClientRegistrationErrorRetry)
                    startClientRegistration(setState)
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

private fun TextHandlerEnvironment.requestInfo(
    state: ChatState.ClientRegistration?
): ClientRegistrationRequest {
    return when {
        state?.name == null -> {
            sendReply(Strings.RequestClientName)
            ClientRegistrationRequest.Name
        }
        state.email == null -> {
            sendReply(Strings.RequestClientEmail)
            ClientRegistrationRequest.Email
        }
        else -> {
            sendReply(
                Strings.clientRegistrationConfirmation(
                    name = state.name,
                    email = state.email
                )
            )
            ClientRegistrationRequest.Confirm
        }
    }
}
