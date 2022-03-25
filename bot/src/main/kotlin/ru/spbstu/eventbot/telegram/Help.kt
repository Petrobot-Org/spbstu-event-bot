package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.telegram.Strings.HelpCommands

fun TextHandlerEnvironment.writeHelp() {
    sendReply(text = HelpCommands)
}