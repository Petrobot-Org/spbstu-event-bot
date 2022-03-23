package ru.spbstu.eventbot.domain.entities

import java.util.*

data class Course(
    val id: Long,
    val title: String,
    val description: String,
    val clientId: Long,
    val expiryDate: GregorianCalendar
)