package ru.spbstu.eventbot.domain.entities

import java.time.LocalDate
import kotlin.math.abs

data class GroupMatchingRules(
    val years: Set<Year> = emptySet(),
    val specialities: Set<Speciality> = emptySet()
) {
    fun toRegex(date: LocalDate): Regex {
        var re: Regex = Regex("0")
        var year: Int = date.year.mod(10)

        // 2ой семестр закончивается в июле
        if (date.monthValue > 6) {
            year++
        }

        // Пустое множество years или specialities означает, что никаких ограничений на них не накладывается
        if (specialities.isEmpty() && years.isEmpty()) {
            re = Regex(pattern = "[взВЗ]?353[0-9]{4}/[0-9]{5}")
        }

        if (!years.isEmpty() && specialities.isEmpty()) {
            re = Regex(pattern = "[взВЗ]?353[0-9]{4}/" + abs(year - years.elementAt(0).value) + "[0-9]{4}")
        }
        if (!specialities.isEmpty() && years.isEmpty()) {
            re = Regex(
                pattern = "[взВЗ]?353" + specialities.elementAt(0).value + "/[0-9]{5}"
            )
        }
        if (!specialities.isEmpty() && !years.isEmpty()) {

            var yearsDate: String = ""
            val itterator = years.iterator()
            while (itterator.hasNext()) {
                yearsDate += abs(year - itterator.next().value)
                if (itterator.hasNext()) {
                    yearsDate += "|"
                }
            }
            var specialitiesDate: String = ""
            val itt = specialities.iterator()
            while (itt.hasNext()) {
                specialitiesDate += itt.next().value
                if (itt.hasNext()) {
                    specialitiesDate += "|"
                }
            }
            println(yearsDate)
            println(specialitiesDate)
            re = Regex(
                pattern = "[взВЗ]?353(" + specialitiesDate + ")/(" + yearsDate + ")[0-9]{4}"
            )
        }

        return re
    }
}
