package ru.spbstu.eventbot.domain.entities

@JvmInline
value class ClientName private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): ClientName? {
            return if (value.isNotBlank()) {
                ClientName(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String = value
}
