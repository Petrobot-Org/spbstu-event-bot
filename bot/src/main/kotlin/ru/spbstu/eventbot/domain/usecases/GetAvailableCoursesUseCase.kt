package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository

class GetAvailableCoursesUseCase(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {
    sealed interface Result {
        data class OK(val courses: List<Course>) : Result
        object NotRegistered : Result
    }

    context(Permissions)
    operator fun invoke(): Result {
        val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
        val courses = courseRepository.getAvailable().filter { it.groupMatcher.matches(student.group.value) }
        return Result.OK(courses)
    }
}
