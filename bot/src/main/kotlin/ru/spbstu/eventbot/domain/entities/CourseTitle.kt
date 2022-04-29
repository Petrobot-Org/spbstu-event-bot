package ru.spbstu.eventbot.domain.entities

@JvmInline
value class CourseTitle private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): CourseTitle? {
            return if (value.isNotBlank()) {
                CourseTitle(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String = value
}
