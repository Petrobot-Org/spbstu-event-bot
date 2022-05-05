package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetCourseByIdUseCase(
    private val courseRepository: CourseRepository
) {

    operator fun invoke(id: CourseId): Course? {
       return courseRepository.getById(id)
    }
}
