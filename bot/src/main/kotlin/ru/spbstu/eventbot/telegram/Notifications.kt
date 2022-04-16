package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import ru.spbstu.eventbot.domain.entities.Course

fun notifyCourseExpired(course: Course, bot: Bot, applicantsTable: ByteArray) {
    val file = TelegramFile.ByByteArray(applicantsTable, "${course.title}.csv")
    val chatId = ChatId.fromId(course.client.userId)
    bot.sendDocument(chatId, file, Strings.courseExpiredNotification(course))
}
