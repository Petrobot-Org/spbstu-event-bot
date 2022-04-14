package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository

class IsApplicationSubmittedUseCase(
    private val applicationRepository: ApplicationRepository,
    private val studentRepository: StudentRepository
) {
    sealed interface Result {
        data class OK(val value: Boolean) : Result
        object NotRegistered : Result
    }

    context(Permissions)
    operator fun invoke(courseId: CourseId): Result {
        val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
        val contains = applicationRepository.contains(student.id, courseId)
        return Result.OK(contains)
    }
}
