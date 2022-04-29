package ru.spbstu.eventbot.domain.entities

@JvmInline
value class CourseDescription private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): CourseDescription? {
            return if (value.isNotBlank()) {
                CourseDescription(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String = value
}
