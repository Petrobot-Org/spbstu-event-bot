package ru.spbstu.eventbot.domain.entities

import java.time.LocalDate
import kotlin.math.abs

data class GroupMatchingRules(
    val years: Set<Year> = emptySet(),
    val specialities: Set<Speciality> = emptySet()
) {
    fun toRegex(date: LocalDate): Regex {
        var re: Regex = Regex(pattern = "[взВЗ]?353[0-9]{4}/[0-9]{5}")
        var year: Int = date.year.mod(10)

        // 2ой семестр закончивается в июле
        if (date.monthValue > 6) {
            year++
        }
        if (!years.isEmpty() && specialities.isEmpty()) {
            re = Regex(pattern = "[взВЗ]?353[0-9]{4}/" + abs(year - years.elementAt(0).value) + "[0-9]{4}")
        }
        if (!specialities.isEmpty() && years.isEmpty()) {
            re = Regex(pattern = "[взВЗ]?353" + specialities.elementAt(0).value + "/[0-9]{5}")
        }
        if (!specialities.isEmpty() && !years.isEmpty()) {
            val yearAdmission: String =
                years
                    .map { y -> abs(year - y.value) }
                    .joinToString(prefix = "(", separator = "|", postfix = ")")
            var specialitie: String =
                specialities
                    .map { sp -> sp.value }
                    .joinToString(prefix = "(", separator = "|", postfix = ")")

            re = Regex(pattern = "[взВЗ]?353" + specialitie + "/" + yearAdmission + "[0-9]{4}")
        }

        return re
    }
}
