package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Client

interface ClientRepository {
    fun insert(name: String, email: String)
    fun findById(chatId: Long): Client?
}
