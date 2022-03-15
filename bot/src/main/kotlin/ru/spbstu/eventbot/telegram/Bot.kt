package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase

class Bot : KoinComponent {
    private val submitApplication: SubmitApplicationUseCase by inject()

    fun start(telegramToken: String) {
        val bot = bot {
            token = telegramToken
            dispatch {
                text("chatid") {
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = message.chat.id.toString())
                }
                text("apply") {
                    submitApplication(message.chat.id, 1)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = "Success")
                }
            }
        }
        bot.startPolling()
    }
}