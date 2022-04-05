package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class CreateNewCourseUseCase(
    private val courseRepository: CourseRepository,
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        object OK : Result
        object NotRegistered : Result
        object InvalidArguments : Result
    }

    operator fun invoke(id: Long, title: String, description: String,additionalQuestion: String, expiryDate: Instant): Result {
        val client = clientRepository.findById(id) ?: return Result.NotRegistered
        courseRepository.insert(clientId = client.id, title = title, description = description, additionalQuestion = additionalQuestion, expiryDate = expiryDate)
        return Result.OK
    }
}
