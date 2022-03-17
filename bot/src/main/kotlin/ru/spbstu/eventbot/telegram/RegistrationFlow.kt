package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.usecases.RegisterUserUseCase

fun TextHandlerEnvironment.handleRegistration(
    state: ChatState.Registration,
    setNewState: (ChatState) -> Unit,
    registerUser: RegisterUserUseCase
) {
    val newState = when (state.request) {
        RegistrationRequest.Name -> {
            if (!registerUser.isNameValid(text)) {
                sendReply(Strings.InvalidName)
                return
            }
            state.copy(name = text)
        }
        RegistrationRequest.Email -> {
            if (!registerUser.isEmailValid(text)) {
                sendReply(Strings.InvalidEmail)
                return
            }
            state.copy(email = text)
        }
        RegistrationRequest.Group -> {
            if (!registerUser.isGroupValid(text)) {
                sendReply(Strings.InvalidGroup)
                return
            }
            state.copy(group = text)
        }
        RegistrationRequest.Confirm -> {
            when (text.lowercase()) {
                in Strings.PositiveAnswers -> {
                    val result = registerUser(message.chat.id, state.name!!, state.email!!, state.group!!)
                    when (result) {
                        RegisterUserUseCase.Result.OK -> {
                            setNewState(ChatState.Empty)
                            sendReply(Strings.RegisteredSuccessfully)
                            return
                        }
                        RegisterUserUseCase.Result.InvalidArguments -> {
                            sendReply(Strings.RegistrationErrorRetry)
                            ChatState.Registration(RegistrationRequest.Name)
                        }
                    }
                }
                in Strings.NegativeAnswers -> {
                    sendReply(Strings.RegistrationRetry)
                    ChatState.Registration(RegistrationRequest.Name)
                }
                else -> {
                    sendReply(Strings.RequestYesNo)
                    return
                }
            }
        }
    }
    val request = requestInfo(newState)
    setNewState(newState.copy(request = request))
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
