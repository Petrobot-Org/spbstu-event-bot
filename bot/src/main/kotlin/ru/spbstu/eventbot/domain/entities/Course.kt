package ru.spbstu.eventbot.domain.entities

import java.time.Instant

data class Course(
    val id: CourseId,
    val title: CourseTitle,
    val description: CourseDescription,
    val additionalQuestion: AdditionalQuestion,
    val client: Client,
    val expiryDate: Instant,
    val groupMatcher: Regex,
    val resultsSent: Boolean
)

@JvmInline
value class CourseId(val value: Long)
