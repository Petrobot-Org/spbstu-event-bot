package ru.spbstu.eventbot.domain.entities

data class Student(
    val id: Long,
    val chatId: Long,
    val email: String,
    val fullName: String,
    val group: String
)
