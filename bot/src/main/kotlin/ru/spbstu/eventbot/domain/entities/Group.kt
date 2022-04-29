package ru.spbstu.eventbot.domain.entities

@JvmInline
value class Group private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): Group? {
            val regex = Regex(pattern = "[взВЗ]?35[3-6]0[29]0[24]/[0-9]{5}")
            return if (regex.matches(value)) {
                Group(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String = value
}
