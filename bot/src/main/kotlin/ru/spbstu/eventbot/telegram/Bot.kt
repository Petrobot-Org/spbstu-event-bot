package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.spbstu.eventbot.domain.usecases.RegisterStudentUseCase
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase

class Bot : KoinComponent {
    private val submitApplication: SubmitApplicationUseCase by inject()
    private val registerStudent: RegisterStudentUseCase by inject()

    private val states = mutableMapOf<Long, ChatState>()

    fun start(telegramToken: String) {
        val bot = bot {
            token = telegramToken
            dispatch {
                text {
                    val state = states[message.chat.id] ?: ChatState.Empty
                    val setNewState = { newState: ChatState -> states[message.chat.id] = newState }
                    if (text.startsWith("/")) {
                        handleCommand(state, setNewState)
                    } else {
                        handleText(state, setNewState)
                    }
                }
            }
        }
        bot.startPolling()
    }

    private fun TextHandlerEnvironment.handleCommand(state: ChatState, setNewState: (ChatState) -> Unit) {
        when (text) {
            "/register" -> startRegistration(setNewState)
            else -> sendReply(Strings.UnknownCommand)
        }
    }

    private fun TextHandlerEnvironment.handleText(state: ChatState, setNewState: (ChatState) -> Unit) {
        when (state) {
            ChatState.Empty -> sendReply(Strings.DontKnowWhatToDo)
            is ChatState.Registration -> handleRegistration(state, setNewState, registerStudent)
        }
    }
}
