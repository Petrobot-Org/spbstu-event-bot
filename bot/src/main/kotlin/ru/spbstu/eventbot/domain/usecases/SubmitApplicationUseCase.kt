package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.UserRepository

class SubmitApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository
    ) {
    operator fun invoke(chatId: Long, courseId: Long) {
        val user = userRepository.findByChatId(chatId)
        applicationRepository.insert(Application(null, user.id, courseId))
    }
}