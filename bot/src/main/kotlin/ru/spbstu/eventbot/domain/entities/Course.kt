package ru.spbstu.eventbot.domain.entities

import java.time.Instant

data class Course(
    val id: Long,
    val title: String,
    val description: String,
    val additionalQuestion: AdditionalQuestion,
    val client: Client,
    val expiryDate: Instant,
    val resultsSent: Boolean
)
