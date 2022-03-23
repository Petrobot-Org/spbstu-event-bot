package ru.spbstu.eventbot.domain.repository

interface ApplicationRepository {
    fun insert(chatId: Long, courseId: Long)
}