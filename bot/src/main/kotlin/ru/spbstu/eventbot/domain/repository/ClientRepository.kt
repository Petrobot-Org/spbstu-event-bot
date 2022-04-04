package ru.spbstu.eventbot.domain.repository

interface ClientRepository{
    fun insert(email: String, name: String)
}
