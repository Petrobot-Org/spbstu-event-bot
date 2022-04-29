package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.StudentRepository

class RegisterStudentUseCase(
    private val studentRepository: StudentRepository,
) {
    sealed interface Result {
        object OK : Result
        object Error : Result
    }

    context(Permissions)
    operator fun invoke(fullName: FullName, email: Email, group: Group): Result {
        val wasInserted = studentRepository.insert(chatId, email, fullName, group)
        return if (wasInserted) Result.OK else Result.Error
    }
}
