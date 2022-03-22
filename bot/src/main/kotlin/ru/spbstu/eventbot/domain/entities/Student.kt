package ru.spbstu.eventbot.domain.entities

data class Student(
    val id: Long,
    val chatId: Long,
    val email: String,
    val name: String,
    val group: String
)
