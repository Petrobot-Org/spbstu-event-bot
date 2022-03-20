package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Client

interface ClientRepository {
    fun findById(id: Long): Client
    fun findByName(full_name: String): Client
    fun findByEmail(email: String): Client
}