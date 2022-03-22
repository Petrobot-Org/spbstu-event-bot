package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository

class SubmitApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
    ) {
    operator fun invoke(chatId: Long, courseName: String) {
        val student = studentRepository.findByChatId(chatId)
        val course = courseRepository.findByName(courseName)
        applicationRepository.insert(Application(null, student.id, course.id))
    }
}