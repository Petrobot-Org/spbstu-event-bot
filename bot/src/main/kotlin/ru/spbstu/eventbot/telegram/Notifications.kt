package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.telegram.Strings.notificationAboutNewCourse

fun notifyCourseExpired(course: Course, bot: Bot, applicantsTable: ByteArray) {
    val file = TelegramFile.ByByteArray(applicantsTable, "${course.title.value.sanitizedFilename()}.xlsx")
    val chatId = ChatId.fromId(course.client.userId)
    bot.sendDocument(chatId, file, Strings.courseExpiredNotification(course))
}

fun notifyNewCourse(course: Course, bot: Bot, students: Iterable<Student>) {
    students.forEach { student ->
        bot.sendMessage(ChatId.fromId(student.chatId), text = notificationAboutNewCourse(course), parseMode = ParseMode.MARKDOWN)
    }
}
