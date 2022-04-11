package ru.spbstu.eventbot.domain.repository


import ru.spbstu.eventbot.domain.entities.Client

interface ClientRepository {
    fun insert(name: String, email: String, userId: Long?)
    fun contains(userId: Long): Boolean
    fun getByUserId(userId: Long): List<Client>
    fun getById(id: Long): Client?
    fun getAll(): List<Client>
}
