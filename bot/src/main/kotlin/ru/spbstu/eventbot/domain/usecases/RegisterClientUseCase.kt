package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.ClientRepository

class RegisterClientUseCase(
    private val clientRepository: ClientRepository,
) {
    val isFullNameValid = IsFullNameValidUseCase
    val isEmailValid = IsEmailValidUseCase

    sealed interface Result {
        object OK : Result
        object Error : Result
        object InvalidArguments : Result
    }

    operator fun invoke(email: String, name: String): Result {
        if (!isEmailValid(email) || !isFullNameValid(name)) {
            return Result.InvalidArguments
        }
        return try {
            clientRepository.insert(email, name)
            Result.OK
        } catch (e: Exception) {
            Result.Error
        }
    }
}
