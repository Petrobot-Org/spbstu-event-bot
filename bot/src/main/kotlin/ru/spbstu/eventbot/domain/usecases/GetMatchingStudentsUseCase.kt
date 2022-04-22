package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.StudentRepository

class GetMatchingStudentsUseCase(
    private val studentRepository: StudentRepository
) {
    operator fun invoke(groupMatcher: Regex): List<Student> {
        return studentRepository.findAll()
            .filter { groupMatcher.matches(it.group.value) }
    }
}
