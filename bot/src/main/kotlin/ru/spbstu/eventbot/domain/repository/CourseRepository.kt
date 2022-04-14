package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.*
import java.time.Instant

interface CourseRepository {
    fun getAvailable(): List<Course>
    fun getById(id: CourseId): Course?
    fun getAvailableCoursesByUserId(userId: Long): List<Course>
    fun insert(clientId: ClientId, title: CourseTitle, description: CourseDescription, additionalQuestion: AdditionalQuestion, expiryDate: Instant)
}
