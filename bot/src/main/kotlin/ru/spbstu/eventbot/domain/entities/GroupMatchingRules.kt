package ru.spbstu.eventbot.domain.entities

data class GroupMatchingRules(
    val years: Set<Year>,
    val specialities: Set<Speciality>
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
            fun valueOf(value: String) = if (true) Speciality(value) else null // TODO: Implement speciality validation
        }
    }

    fun toRegex(): Regex {
        return Regex("") // TODO: Implement regex conversion
    }
}
