package ru.spbstu.eventbot.domain.entities

data class Student(
    val id: Long,
    val chatId: Long,
    val email: Email,
    val fullName: FullName,
    val group: Group
)
