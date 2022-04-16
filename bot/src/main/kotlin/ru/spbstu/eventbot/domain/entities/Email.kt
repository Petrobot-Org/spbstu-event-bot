package ru.spbstu.eventbot.domain.entities

import org.apache.commons.validator.routines.EmailValidator

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): Email? {
            return if (EmailValidator.getInstance().isValid(value)) {
                Email(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String = value
}
