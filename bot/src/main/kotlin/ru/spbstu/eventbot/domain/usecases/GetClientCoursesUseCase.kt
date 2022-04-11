package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetClientCoursesUseCase(
    private val courseRepository: CourseRepository
) {
    sealed interface Result {
        data class OK(val courses: List<Course>) : Result
        object Unauthorized : Result
    }

    context(Permissions)
    operator fun invoke(): Result = when {
        canAccessAnyCourse -> Result.OK(courseRepository.getAvailable())
        canAccessTheirCourse -> Result.OK(courseRepository.getAvailableCoursesByUserId(userId))
        else -> Result.Unauthorized
    }
}
