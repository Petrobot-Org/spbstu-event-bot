package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Student

interface StudentRepository {
    fun findByChatId(chatId: Long): Student
    fun insert(chatId: Long, email: String, name: String, group: String)
}
