package ru.spbstu.eventbot.domain.entities

data class Application(
    val id: ApplicationId,
    val student: Student,
    val courseId: CourseId,
    val additionalInfo: String?
)

@JvmInline
value class ApplicationId(val value: Long)
