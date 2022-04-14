package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository

class RevokeApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val studentRepository: StudentRepository
) {
    sealed interface Result {
        object OK : Result
        object NotRegistered : Result
    }

    context(Permissions)
    operator fun invoke(courseId: CourseId): Result {
        val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
        applicationRepository.delete(studentId = student.id, courseId = courseId)
        return Result.OK
    }
}
