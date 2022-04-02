package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.CourseRepository
import ru.spbstu.eventbot.domain.repository.ClientRepository
import java.time.Instant

class CreateNewCourseUseCase (
    private val courseRepository: CourseRepository,
    private val clientRepository: ClientRepository
    ) {
        sealed interface Result {
            object OK : Result
            object NotRegistered : Result
        }

        operator fun invoke(chatId: Long, title: String, description: String,  expiryDate: Instant): Result {
            val client = clientRepository.findByChatId(chatId) ?: return Result.NotRegistered
            courseRepository.insert(title=title,  description=description, clientId=client.id, expiryDate = expiryDate)
            return Result.OK
        }
}