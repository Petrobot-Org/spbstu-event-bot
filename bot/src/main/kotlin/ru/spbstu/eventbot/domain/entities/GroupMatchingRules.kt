package ru.spbstu.eventbot.domain.entities

data class GroupMatchingRules(
    val years: Set<Year>,
    val specialities: Set<Speciality>
) {
    @JvmInline
    value class Year private constructor(val value: Int) {
        companion object {
            fun valueOf(value: Int): Year? {
                return if (value in 1..4) {
                    Year(value)
                } else {
                    null
                }
            }
        }
    }

    @JvmInline
    value class Speciality private constructor(val value: String) {
        companion object {
            fun valueOf(value: String): Speciality? {
                return if (true) { // TODO: Implement validation of speciality code
                    Speciality(value)
                } else {
                    null
                }
            }
        }
    }

    fun toRegex(): Regex {
        return Regex("") // TODO: Implement regex conversion
    }
}
