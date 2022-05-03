package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId

context(TextHandlerEnvironment)
fun sendSecret() {
    val stickerSet = bot.getStickerSet("nu_mem_zhe").first?.body()?.result ?: return
    bot.sendSticker(
        chatId = ChatId.fromId(message.chat.id),
        sticker = stickerSet.stickers.random().fileId,
        replyMarkup = null
    )
}
