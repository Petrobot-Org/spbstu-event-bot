package ru.spbstu.eventbot.domain.entities

@JvmInline
value class Speciality private constructor(val value: String) {
    companion object {
        fun valueOf(value: String) = if (Regex("\\d{4}").matches(value)) Speciality(value) else null
    }
}
