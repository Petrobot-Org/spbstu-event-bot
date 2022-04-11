package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Client

interface ClientRepository {
    fun getClientsByUserId(userId: Long): List<Client>
    fun insert(userId: Long, name: String, email: String)
}
