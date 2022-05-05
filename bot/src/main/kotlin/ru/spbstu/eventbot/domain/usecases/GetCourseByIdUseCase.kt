package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetCourseByIdUseCase(
    private val courseRepository: CourseRepository
) {
    sealed interface Result {
        data class OK(val course: Course) : Result
        object NoSuchCourse : Result
    }
    operator fun invoke(id: CourseId):Result {
        val course = courseRepository.getById(id)?: return Result.NoSuchCourse
        return Result.OK(course)
    }
}
