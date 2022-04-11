package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.telegram.Strings.courseExpiredNotification

fun notifyCourseExpired(course: Course, bot: Bot, byteArray: ByteArray) {
    val telFile: TelegramFile = TelegramFile.ByByteArray(byteArray, "Hello_World")
    val chatId: ChatId = ChatId.fromId(course.client.userId ?: return)
    bot.sendDocument(chatId, telFile, courseExpiredNotification(course))
}