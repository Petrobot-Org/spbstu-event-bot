package ru.spbstu.eventbot.domain.entities

data class Client(
    val id: Long,
    val idProject: Long,
    val email: String,
    val fullName: String,
)