package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.squareup.sqldelight.Query
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.usecases.CourseUseCase
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase

class Bot : KoinComponent {
    private val submitApplication: SubmitApplicationUseCase by inject()
    private val courseUseCase: CourseUseCase by inject()

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
                text("see current courses") {
                    val query: List<Course> = courseUseCase.findAll()
                    val text: StringBuilder = java.lang.StringBuilder("Courses:\n")
                    for (elem in query) {
                        text.append(elem.id)
                        text.append(") ")
                        text.append(elem.name)
                        text.append('\n')
                    }
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = text.toString())
                }
            }
        }
        bot.startPolling()
    }
}