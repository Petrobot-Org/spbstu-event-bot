package ru.spbstu.eventbot.domain.entities

data class Student(
    val id: StudentId,
    val chatId: Long,
    val email: Email,
    val fullName: FullName,
    val group: Group
)

@JvmInline
value class StudentId(val value: Long)
