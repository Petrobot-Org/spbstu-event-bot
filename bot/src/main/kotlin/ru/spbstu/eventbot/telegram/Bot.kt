package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.ChatId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.spbstu.eventbot.domain.usecases.RegisterUserUseCase
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase

class Bot : KoinComponent {
    private val submitApplication: SubmitApplicationUseCase by inject()
    private val registerUser: RegisterUserUseCase by inject()

    private val states = mutableMapOf<Long, ChatState>()

    fun start(telegramToken: String) {
        val bot = bot {
            token = telegramToken
            dispatch {
                command("register") {
                    states[message.chat.id] = ChatState.Registration(RegistrationRequest.Name)
                    bot.sendMessage(ChatId.fromId(message.chat.id), Strings.RequestName)
                }
                text("chatid") {
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = message.chat.id.toString())
                }
                text("apply") {
                    submitApplication(message.chat.id, 1)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = "Success")
                }
                text {
                    if (text.startsWith("/")) {
                        return@text
                    }
                    val state = states[message.chat.id] ?: ChatState.Empty
                    val setNewState = { newState: ChatState -> states[message.chat.id] = newState }
                    when (state) {
                        ChatState.Empty -> sendReply(Strings.DontKnowWhatToDo)
                        is ChatState.Registration -> handleRegistration(state, setNewState, registerUser)
                    }
                }
            }
        }
        bot.startPolling()
    }
}
