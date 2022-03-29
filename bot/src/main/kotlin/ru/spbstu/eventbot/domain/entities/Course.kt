package ru.spbstu.eventbot.domain.entities

import java.time.Instant

data class Course(
    val id: Long,
    val title: String,
    val description: String,
    val clientId: Long,
    val expiryDate: Instant,
    val resultsSent: Boolean
)
