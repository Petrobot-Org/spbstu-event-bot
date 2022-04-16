package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.GetApplicationsByCourseIdUseCase

class CreateApplicantsTable(private val getApplicants: GetApplicationsByCourseIdUseCase) {
    context(Permissions)
    operator fun invoke(course: Course): ByteArray {
        return "Здесь таблица"
            .split("")
            .joinToString(", ")
            .encodeToByteArray()
    }
}
