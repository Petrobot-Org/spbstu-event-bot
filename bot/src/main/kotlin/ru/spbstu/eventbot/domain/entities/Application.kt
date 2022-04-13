package ru.spbstu.eventbot.domain.entities

data class Application(
    val id: Long,
    val student: Student,
    val courseId: Long,
    val additionalInfo: String?
)
