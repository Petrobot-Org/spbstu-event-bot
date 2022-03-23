package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.StudentRepository
import java.sql.SQLException

class RegisterStudentUseCase(
    private val studentRepository: StudentRepository
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
            studentRepository.insert(chatId, email, name, group)
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
