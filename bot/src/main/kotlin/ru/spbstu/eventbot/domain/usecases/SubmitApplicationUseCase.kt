package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class SubmitApplicationUseCase(
    private val applicationRepository: ApplicationRepository
) {
    operator fun invoke(chatId: Long, courseId: Long) {
        applicationRepository.insert(chatId = chatId, courseId = courseId)
    }
}
