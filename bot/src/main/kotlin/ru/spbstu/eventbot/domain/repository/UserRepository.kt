package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.User

interface UserRepository {
    fun findByChatId(chatId: Long): User
    fun insert(chatId: Long, email: String, name: String, group: String)
}
