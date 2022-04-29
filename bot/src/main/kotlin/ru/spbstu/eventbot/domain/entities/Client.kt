package ru.spbstu.eventbot.domain.entities

data class Client(
    val id: ClientId,
    val email: Email,
    val name: ClientName,
    val userId: Long
)

@JvmInline
value class ClientId(val value: Long)
