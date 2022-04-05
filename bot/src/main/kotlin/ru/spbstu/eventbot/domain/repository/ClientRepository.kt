package ru.spbstu.eventbot.domain.repository

interface ClientRepository {
    fun insert(name: String, email: String, userId: Long?)
    fun contains(userId: Long): Boolean
}
