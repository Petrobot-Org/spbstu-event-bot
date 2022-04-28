package ru.spbstu.eventbot.domain.entities

@JvmInline
value class Year private constructor(val value: Int) {
    companion object {
        fun valueOf(value: Int) = if (value in 1..10) Year(value) else null
    }
}
