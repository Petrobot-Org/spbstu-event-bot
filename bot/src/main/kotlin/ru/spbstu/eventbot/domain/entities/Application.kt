package ru.spbstu.eventbot.domain.entities

data class Application(
    val id: Long?,
    val studentId: Long,
    val courseId: Long
)