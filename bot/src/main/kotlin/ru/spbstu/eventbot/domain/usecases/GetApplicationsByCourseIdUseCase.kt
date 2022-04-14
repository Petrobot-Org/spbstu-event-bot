package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetApplicationsByCourseIdUseCase(
    private val applicationRepository: ApplicationRepository,
    private val courseRepository: CourseRepository
) {
    sealed interface Result {
        data class OK(val applications: List<Application>) : Result
        object NoSuchCourse : Result
        object Unauthorized : Result
    }

    context(Permissions)
    operator fun invoke(courseId: CourseId): Result {
        val course = courseRepository.getById(courseId) ?: return Result.NoSuchCourse
        val isPermitted = canAccessAnyCourse || (canAccessTheirCourse && course.client.userId == userId)
        if (!isPermitted) {
            return Result.Unauthorized
        }
        return Result.OK(applicationRepository.getApplications(courseId))
    }
}
