package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.usecases.GetApplicantsByCourseIdUseCase
import ru.spbstu.eventbot.domain.permissions.Permissions

class CreateApplicantsTable(getApplicants: GetApplicantsByCourseIdUseCase) {
    context(Permissions)
    operator fun invoke(clientId: Long): ByteArray {
        val str: String = "Hello, World!" //доработать здесь
        return str.encodeToByteArray()
    }
}
