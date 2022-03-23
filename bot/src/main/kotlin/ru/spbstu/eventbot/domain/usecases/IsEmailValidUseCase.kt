package ru.spbstu.eventbot.domain.usecases

import org.apache.commons.validator.routines.EmailValidator

object IsEmailValidUseCase {
    operator fun invoke(email: String): Boolean {
        return EmailValidator.getInstance().isValid(email)
    }
}
