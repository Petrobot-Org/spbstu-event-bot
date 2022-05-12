package ru.spbstu.eventbot.domain.entities

import java.time.LocalDate
import kotlin.math.abs

data class GroupMatchingRules(
    val years: Set<Year> = emptySet(),
    val specialities: Set<Speciality> = emptySet()
) {
    fun toRegex(date: LocalDate): Regex {
        val year = date.plusMonths(6).year
        val enrollYears = years.map { (year - it.value).mod(10) }
        val YearPattern = enrollYears.joinToString("|")
        val SpecialitiesPattern = specialities.joinToString("|")
        return Regex(".*($SpecialitiesPattern)/($YearPattern).*")

    }
}
