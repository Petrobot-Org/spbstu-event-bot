package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.repository.StudentRepository
import java.sql.SQLException

class RegisterStudentUseCase(
    private val studentRepository: StudentRepository,
) {
    val isFullNameValid = IsFullNameValidUseCase
    val isEmailValid = IsEmailValidUseCase
    val isGroupValid = IsGroupValidUseCase

    sealed interface Result {
        object OK : Result
        object Error : Result
        object InvalidArguments : Result
    }

    operator fun invoke(chatId: Long, fullName: String, email: String, group: String): Result {
        if (!isFullNameValid(fullName) || !isEmailValid(email) || !isGroupValid(group)) {
            return Result.InvalidArguments
        }
        return try {
            studentRepository.insert(chatId, email, fullName, group)
            Result.OK
        } catch (e: SQLException) {
            Result.Error
        }
    }
}
