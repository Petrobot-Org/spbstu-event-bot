package ru.spbstu.eventbot.domain.entities

import java.time.LocalDateTime

data class Course(
    val id: Long,
    val title: String,
    val description: String,
    val clientId: Long,
    val expiryDate: LocalDateTime,
    val resultsSent: Boolean
)
