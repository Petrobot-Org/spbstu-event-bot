package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.entities.ClientName
import ru.spbstu.eventbot.domain.entities.Email

interface ClientRepository {
    fun insert(name: ClientName, email: Email, userId: Long?)
    fun contains(userId: Long): Boolean
    fun getByUserId(userId: Long): List<Client>
    fun getById(id: Long): Client?
    fun getAll(): List<Client>
}
