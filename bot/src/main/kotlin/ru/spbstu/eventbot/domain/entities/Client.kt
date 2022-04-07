package ru.spbstu.eventbot.domain.entities

data class Client(
    val id: Long,
    val userId: Long?,
    val email: String,
    val name: String
)
