package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository

class GetApplicantsByCourseIdUseCase(
    private val applicationRepository: ApplicationRepository,
    private val courseRepository: CourseRepository
) {
    sealed interface Result {
        data class OK(val applicants: List<Student>) : Result
        object NoSuchCourse : Result
        object Unauthorized : Result
    }

    context(Permissions)
    operator fun invoke(courseId: Long): Result {
        val course = courseRepository.getById(courseId) ?: return Result.NoSuchCourse
        val isPermitted = canAccessAnyCourse || (canAccessTheirCourse && course.client.userId == userId)
        if (!isPermitted) {
            return Result.Unauthorized
        }
        return Result.OK(applicationRepository.getListOfApplicants(courseId))
    }
}
