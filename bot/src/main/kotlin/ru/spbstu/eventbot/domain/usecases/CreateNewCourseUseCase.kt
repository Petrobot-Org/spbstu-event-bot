package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.ClientRepository

class CreateNewCourseUseCase {
    private val courseRepository: CourseRepository,
    private val clienRepository: ClientRepository
    ) {
        sealed interface Result {
            object OK : Result
            object Expired : Result
            object NotRegistered : Result
            object AlreadySubmitted : Result
        }

        operator fun invoke(chatId: Long, courseId: Long): Result {
            val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
            applicationRepository.insert(studentId = student.id, courseId = courseId)
            return Result.OK
        }
}