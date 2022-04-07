package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository
import java.time.Instant
import java.util.*

class SubmitApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val clientRepository: ClientRepository,
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {
    sealed interface Result {
        object OK : Result
        object Expired : Result
        object NotRegistered : Result
        object AlreadySubmitted : Result
    }

    operator fun invoke(chatId: Long, courseId: Long): Result {
        val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
        val course = courseRepository.getById(courseId) ?: return Result.NotRegistered
        val timeNow: Instant = Calendar.getInstance().toInstant()
        if (course.expiryDate.compareTo(timeNow) > 1) {
            return Result.Expired
        }
        if (applicationRepository.containsApplication(chatId, courseId)) {
            return Result.AlreadySubmitted
        }
        applicationRepository.insert(studentId = student.id, courseId = courseId)
        return Result.OK
    }
}
