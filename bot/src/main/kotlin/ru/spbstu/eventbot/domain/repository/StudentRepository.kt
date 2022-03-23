package ru.spbstu.eventbot.domain.repository

interface StudentRepository {
    fun insert(chatId: Long, email: String, fullName: String, group: String)
}
