package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ClientRepository

class GetMyClientsUseCase(
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        data class OK(val clients: List<Client>) : Result
        object Unauthorized : Result
    }

    context(Permissions)
    operator fun invoke(): Result = when {
        canAccessAnyCourse -> Result.OK(clientRepository.getAll())
        canAccessTheirCourse -> Result.OK(clientRepository.getByUserId(userId))
        else -> Result.Unauthorized
    }
}
