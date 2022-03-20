package ru.spbstu.eventbot.domain.entities

data class Client(
    val id: Long,
    val id_project: Long,
   val email: String,
    val full_name: String,
)