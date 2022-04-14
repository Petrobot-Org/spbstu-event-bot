package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.AdditionalQuestion
import ru.spbstu.eventbot.domain.entities.ClientId
import ru.spbstu.eventbot.domain.entities.CourseDescription
import ru.spbstu.eventbot.domain.entities.CourseTitle
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class CreateNewCourseUseCase(
    private val courseRepository: CourseRepository,
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        object OK : Result
        object InvalidArguments : Result
        object NoSuchClient : Result
        object Unauthorized : Result
    }

    context(Permissions)
    operator fun invoke(
        clientId: ClientId,
        title: CourseTitle,
        description: CourseDescription,
        additionalQuestion: AdditionalQuestion,
        expiryDate: Instant
    ): Result {
        val client = clientRepository.getById(clientId) ?: return Result.NoSuchClient
        val isPermitted = canAccessAnyCourse || (canAccessTheirCourse && client.userId == userId)
        if (!isPermitted) {
            return Result.Unauthorized
        }
        courseRepository.insert(clientId = clientId, title = title, description = description, additionalQuestion = additionalQuestion, expiryDate = expiryDate)
        return Result.OK
    }
}
