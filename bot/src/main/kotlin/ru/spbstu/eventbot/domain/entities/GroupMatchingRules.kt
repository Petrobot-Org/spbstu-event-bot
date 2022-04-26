package ru.spbstu.eventbot.domain.entities

import java.time.LocalDate

data class GroupMatchingRules(
    val years: Set<Year> = emptySet(),
    val specialities: Set<Speciality> = emptySet()
) {
    @JvmInline
    value class Year private constructor(val value: Int) {
        companion object {
            fun valueOf(value: Int) = if (value in 1..4) Year(value) else null
        }
    }

    @JvmInline
    value class Speciality private constructor(val value: String) {
        companion object {
            fun valueOf(value: String) = if (Regex("\\d{4}").matches(value)) Speciality(value) else null
        }
    }

    // TODO: Реализовать
    fun toRegex(date: LocalDate): Regex {
        // Пустое множество years или specialities означает, что никаких ограничений на них не накладывается
        return Regex("")
    }
}
