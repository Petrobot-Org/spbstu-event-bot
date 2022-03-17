package ru.spbstu.eventbot.domain.entities

data class User(
    val id: Long,
    val chat_id: Long,
    val email: String,
    val name: String,
    val group: String
)
