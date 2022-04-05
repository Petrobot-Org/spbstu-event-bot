package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetAvailableCoursesByClientIdUseCase(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(clientId: Long): List<Course>{
        return courseRepository.getAvailableCoursesByClientId(clientId)
    }
}