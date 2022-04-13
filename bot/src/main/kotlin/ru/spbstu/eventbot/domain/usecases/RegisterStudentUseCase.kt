package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.StudentRepository

class RegisterStudentUseCase(
    private val studentRepository: StudentRepository,
) {
    context(Permissions)
    operator fun invoke(fullName: FullName, email: Email, group: Group) {
        studentRepository.insert(chatId, email, fullName, group)
    }
}
