package ru.spbstu.eventbot.domain.usecases

object IsGroupValidUseCase {
    operator fun invoke(group: String): Boolean {
        val regex = Regex(pattern = "[взВЗ]?35[3-6]0[29]0[24]/[0-9]{5}")
        return regex.matches(input = group)
    }
}
