package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.ApplicationRepository
import ru.spbstu.eventbot.domain.repository.StudentRepository

class GetApplicantsByCourseIdUseCase
    (private val applicationRepository: ApplicationRepository,
    private val studentRepository: StudentRepository
) {
    operator fun invoke(courseId: Long): List<Student?>? {
       val applications=applicationRepository.getListOfApplicants(courseId)
        var applicants = listOf<Student?>()
        for (student in applications)
        {
            applicants+=studentRepository.findById(student.studentId)
        }
        return applicants
    }
}