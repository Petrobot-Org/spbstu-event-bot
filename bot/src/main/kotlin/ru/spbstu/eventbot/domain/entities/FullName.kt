package ru.spbstu.eventbot.domain.entities

@JvmInline
value class FullName private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): FullName? {
            val regex = Regex(pattern = "([А-ЯЁ][а-яё]+[\\-\\s]?){2,}")
            return if (regex.matches(value)) {
                FullName(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String = value
}
