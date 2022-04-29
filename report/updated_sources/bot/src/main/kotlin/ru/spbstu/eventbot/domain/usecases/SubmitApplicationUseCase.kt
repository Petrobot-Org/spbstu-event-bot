package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository
import java.time.Instant

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
        object NoSuchCourse : Result
        data class AdditionalInfoRequired(val question: String) : Result
    }

    context(Permissions)
    operator fun invoke(courseId: Long, additionalInfo: String? = null): Result {
        val student = studentRepository.findByChatId(chatId) ?: return Result.NotRegistered
        val course = courseRepository.getById(courseId) ?: return Result.NoSuchCourse
        if (course.additionalQuestion.value != null && additionalInfo == null) {
            return Result.AdditionalInfoRequired(course.additionalQuestion.value)
        }
        val timeNow: Instant = Instant.now()
        if (timeNow.isAfter(course.expiryDate)) {
            return Result.Expired
        }
        if (applicationRepository.contains(student.id, courseId)) {
            return Result.AlreadySubmitted
        }
        applicationRepository.insert(studentId = student.id, courseId = courseId, additionalInfo = additionalInfo)
        return Result.OK
    }
}
