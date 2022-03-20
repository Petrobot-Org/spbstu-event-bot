package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.UserRepository

class SubmitApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
    ) {
    operator fun invoke(chatId: Long, courseName: String) {
        val user = userRepository.findByChatId(chatId)
        val course = courseRepository.findByName(courseName)
        applicationRepository.insert(Application(null, user.id, course.id))
    }
}