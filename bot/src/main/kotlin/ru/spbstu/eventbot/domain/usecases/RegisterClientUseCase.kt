package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.permissions.Permissions

class RegisterClientUseCase(
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        object OK : Result
        object InvalidArguments : Result
        object Unauthorized : Result
    }

    val isNameValid = IsClientNameValid
    val isEmailValid = IsEmailValidUseCase

    context(Permissions)
    operator fun invoke(name: String, email: String, userId: Long?): Result {
        if (!canModifyClients) {
            return Result.Unauthorized
        }
        if (!isNameValid(name) || !isEmailValid(email)) {
            return Result.InvalidArguments
        }
        clientRepository.insert(name = name, email = email, userId = userId)
        return Result.OK
    }
}
