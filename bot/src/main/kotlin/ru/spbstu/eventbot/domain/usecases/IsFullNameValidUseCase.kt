package ru.spbstu.eventbot.domain.usecases

object IsFullNameValidUseCase {
    operator fun invoke(fullName: String): Boolean {
        val regex = Regex(pattern = "([А-ЯЁ][а-яё]+[\\-\\s]?){2,}")
        return regex.matches(input = fullName)
    }
}
