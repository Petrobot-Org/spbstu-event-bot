package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.StudentRepository

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

    context(Permissions)
    operator fun invoke(fullName: String, email: String, group: String): Result {
        if (!isFullNameValid(fullName) || !isEmailValid(email) || !isGroupValid(group)) {
            return Result.InvalidArguments
        }
        return try {
            studentRepository.insert(chatId, email, fullName, group)
            Result.OK
        } catch (e: Exception) {
            Result.Error
        }
    }
}
