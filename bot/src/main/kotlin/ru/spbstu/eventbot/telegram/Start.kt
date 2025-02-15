package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.telegram.Strings.HelpStart

context(Permissions)
fun TextHandlerEnvironment.writeStart() {
    val keyboard = buildList {
        val studentButtons = listOf(
            KeyboardButton(Strings.ButtonCourses),
            KeyboardButton(Strings.ButtonRegister)
        )
        val clientButtons = buildList {
            if (canAccessTheirCourse || canAccessAnyCourse) {
                add(KeyboardButton(Strings.ButtonNewCourse))
            }
        }
        val operatorButtons = buildList {
            if (canModifyClients) add(KeyboardButton(Strings.ButtonNewClient))
        }
        add(studentButtons)
        if (clientButtons.isNotEmpty()) add(clientButtons)
        if (operatorButtons.isNotEmpty()) add(operatorButtons)
    }
    sendReply(
        text = HelpStart,
        parseMode = ParseMode.MARKDOWN,
        replyMarkup = KeyboardReplyMarkup(
            keyboard = keyboard,
            resizeKeyboard = true
        )
    )
}
