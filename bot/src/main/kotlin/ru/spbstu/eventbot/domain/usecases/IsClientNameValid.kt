package ru.spbstu.eventbot.domain.usecases

object IsClientNameValid {
    operator fun invoke(name: String): Boolean {
        return name.isNotEmpty()
    }
}
