package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Student

interface StudentRepository {
    fun insert(chatId: Long, email: String, fullName: String, group: String)
    fun findByChatId(chatId: Long): Student?
    fun findById(id: Long): Student?
}
