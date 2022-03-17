package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.UserRepository

class RegisterUserUseCase(
    private val userRepository: UserRepository
) {
    sealed interface Result {
        object OK : Result
        object InvalidArguments : Result
    }

    operator fun invoke(chatId: Long, name: String, email: String, group: String): Result {
        if (!isNameValid(name) || !isEmailValid(email) || !isGroupValid(group)) {
            return Result.InvalidArguments
        }
        userRepository.insert(chatId, email, name, group)
        return Result.OK
    }

    fun isNameValid(text: String): Boolean {
        return true // TODO: Implement name validation
    }

    fun isEmailValid(text: String): Boolean {
        return true // TODO: Implement email validation
    }

    fun isGroupValid(text: String): Boolean {
        return true // TODO: Implement group validation
    }
}
