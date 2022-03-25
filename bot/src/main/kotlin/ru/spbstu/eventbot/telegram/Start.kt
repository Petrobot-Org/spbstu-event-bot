package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.telegram.Strings.HelpStart

fun TextHandlerEnvironment.writeStart() {
    sendReply(text = HelpStart)
}