package ru.spbstu.eventbot.domain.usecases

import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class GetApplicantsByCourseIdUseCase(
    private val applicationRepository: ApplicationRepository
) {
    operator fun invoke(courseId: Long): List<Student> {
        return applicationRepository.getListOfApplicants(courseId)
    }
}
