package ru.spbstu.eventbot.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.entities.AdditionalQuestion
import ru.spbstu.eventbot.domain.entities.Course
import java.time.Instant

interface CourseRepository {
    fun getAvailable(): List<Course>
    fun getById(id: CourseId): Course?
    fun getAvailableCoursesByUserId(userId: Long): List<Course>
    fun getEarliestUnsent(): Flow<Course?>
    fun updateResultsSent(id: CourseId, value: Boolean)
    fun insert(clientId: ClientId, title: CourseTitle, description: CourseDescription, additionalQuestion: AdditionalQuestion, expiryDate: Instant): CourseId?
}
