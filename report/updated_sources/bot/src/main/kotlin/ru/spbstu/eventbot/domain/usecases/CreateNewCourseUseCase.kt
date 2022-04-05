package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class CreateNewCourseUseCase(
    private val courseRepository: CourseRepository
) {
    sealed interface Result {
        object OK : Result
        object InvalidArguments : Result
    }

    operator fun invoke(id: Long, title: String, description: String, additionalQuestion: String, expiryDate: Instant): Result {
        courseRepository.insert(clientId = id, title = title, description = description, additionalQuestion = additionalQuestion, expiryDate = expiryDate)
        return Result.OK
    }
}
