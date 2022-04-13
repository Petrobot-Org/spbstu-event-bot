package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup

context(TextHandlerEnvironment)
fun sendReply(
    text: String,
    parseMode: ParseMode? = null,
    disableWebPagePreview: Boolean? = null,
    disableNotification: Boolean? = null,
    replyToMessageId: Long? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: ReplyMarkup? = null
) {
    bot.sendMessage(
        chatId = ChatId.fromId(message.chat.id),
        text = text,
        parseMode = parseMode,
        disableWebPagePreview = disableWebPagePreview,
        disableNotification = disableNotification,
        replyToMessageId = replyToMessageId,
        allowSendingWithoutReply = allowSendingWithoutReply,
        replyMarkup = replyMarkup
    )
}

context(CallbackQueryHandlerEnvironment)
fun sendReply(
    text: String,
    parseMode: ParseMode? = null,
    disableWebPagePreview: Boolean? = null,
    disableNotification: Boolean? = null,
    replyToMessageId: Long? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: ReplyMarkup? = null
) {
    bot.sendMessage(
        chatId = ChatId.fromId(callbackQuery.message?.chat?.id ?: return),
        text = text,
        parseMode = parseMode,
        disableWebPagePreview = disableWebPagePreview,
        disableNotification = disableNotification,
        replyToMessageId = replyToMessageId,
        allowSendingWithoutReply = allowSendingWithoutReply,
        replyMarkup = replyMarkup
    )
}

context(TextHandlerEnvironment)
fun require(condition: Boolean, action: () -> Unit) {
    if (condition) {
        action()
    } else {
        sendReply(Strings.UnauthorizedError)
    }
}

context(CallbackQueryHandlerEnvironment)
fun require(condition: Boolean, action: () -> Unit) {
    if (condition) {
        action()
    } else {
        sendReply(Strings.UnauthorizedError)
    }
}
