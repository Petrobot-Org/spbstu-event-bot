package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.ClientName
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ClientRepository

class RegisterClientUseCase(
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        object OK : Result
        object Unauthorized : Result
    }

    context(Permissions)
    operator fun invoke(name: ClientName, email: Email, userId: Long?): Result {
        if (!canModifyClients) {
            return Result.Unauthorized
        }
        clientRepository.insert(name = name, email = email, userId = userId)
        return Result.OK
    }
}
