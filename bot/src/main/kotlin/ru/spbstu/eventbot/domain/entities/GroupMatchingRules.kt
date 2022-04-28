package ru.spbstu.eventbot.domain.entities

import java.time.LocalDate

data class GroupMatchingRules(
    val years: Set<Year> = emptySet(),
    val specialities: Set<Speciality> = emptySet()
) {
    // TODO: Реализовать
    fun toRegex(date: LocalDate): Regex {
        // Пустое множество years или specialities означает, что никаких ограничений на них не накладывается
        return Regex("TODO${hashCode()}")
    }
}
