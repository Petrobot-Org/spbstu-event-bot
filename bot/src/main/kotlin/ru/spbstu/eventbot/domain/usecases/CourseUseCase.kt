package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository

class CourseUseCase(
    private val courseRepository: CourseRepository
) {
    fun findAll(): List<Course> {
        return courseRepository.selectAll()
    }
}