package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.ClientRepository

class RegisterClientUseCase(
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        object OK : Result
        object InvalidArguments : Result
    }

    val isNameValid = IsClientNameValid
    val isEmailValid = IsEmailValidUseCase

    operator fun invoke(name: String, email: String): Result {
        if (!isNameValid(name) || !isEmailValid(email)) {
            return Result.InvalidArguments
        }
        clientRepository.insert(name = name, email = email)
        return Result.OK
    }
}
