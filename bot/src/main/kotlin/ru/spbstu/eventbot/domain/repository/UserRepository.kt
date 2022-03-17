package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.User

interface UserRepository {
    fun findByChatId(chatId: Long): User
    fun insert(chatId: Long, name: String, email: String, group: String)
}
