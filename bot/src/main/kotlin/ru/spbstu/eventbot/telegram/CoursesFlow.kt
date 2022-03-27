package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.usecases.GetAvailableCoursesUseCase

fun TextHandlerEnvironment.displayCourses(getAvailableCourses: GetAvailableCoursesUseCase) {
    val courses = getAvailableCourses()
    val buttons = courses.map {
        listOf(InlineKeyboardButton.CallbackData(it.title, "details ${it.id}"))
    }
    sendReply(text = Strings.AvailableCoursesHeader, replyMarkup = InlineKeyboardMarkup.create(buttons))
}
