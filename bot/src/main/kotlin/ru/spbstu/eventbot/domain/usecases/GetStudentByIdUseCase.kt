package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.StudentRepository

class GetStudentByIdUseCase(
    private val studentRepository: StudentRepository
) {
    operator fun invoke(id: Long): Student? {
        return studentRepository.findByChatId(id)
    }
}
