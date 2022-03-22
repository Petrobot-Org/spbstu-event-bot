package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.StudentRepository
import java.sql.SQLException

class RegisterUserUseCase(
    private val userRepository: StudentRepository
) {
    sealed interface Result {
        object OK : Result
        object Error : Result
        object InvalidArguments : Result
    }

    operator fun invoke(chatId: Long, name: String, email: String, group: String): Result {
        if (!isNameValid(name) || !isEmailValid(email) || !isGroupValid(group)) {
            return Result.InvalidArguments
        }
        return try {
            userRepository.insert(chatId, email, name, group)
            Result.OK
        } catch (e: SQLException) {
            Result.Error
        }
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
