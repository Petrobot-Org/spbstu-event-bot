package ru.spbstu.eventbot.domain.entities

data class Application(
    val id: Long?,
    val userId: Long,
    val courseId: Long
)