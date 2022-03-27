package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetAvailableCoursesUseCase(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(): List<Course> {
        return courseRepository.getAvailable()
    }
}
