package ru.spbstu.eventbot.domain.entities

data class Client(
    val id: Long,
    val email: Email,
    val name: ClientName,
    val userId: Long?
)
