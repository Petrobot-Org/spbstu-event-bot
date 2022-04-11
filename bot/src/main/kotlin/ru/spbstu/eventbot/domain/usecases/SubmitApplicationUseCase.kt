package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository

class SubmitApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val studentRepository: StudentRepository
) {
    sealed interface Result {
        object OK : Result
        object Expired : Result
        object NotRegistered : Result
        object AlreadySubmitted : Result
    }

    context(Permissions)
    operator fun invoke(courseId: Long): Result {
        val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
        applicationRepository.insert(studentId = student.id, courseId = courseId)
        return Result.OK
    }
}
