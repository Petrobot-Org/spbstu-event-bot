package ru.spbstu.eventbot.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.spbstu.eventbot.domain.entities.AdditionalQuestion
import ru.spbstu.eventbot.domain.entities.Course
import java.time.Instant

interface CourseRepository {
    fun getAvailable(): List<Course>
    fun getById(id: Long): Course?
    fun getAvailableCoursesByUserId(userId: Long): List<Course>
    fun getEarliestUnsent(): Flow<Course?>
    fun insert(clientId: Long, title: String, description: String, additionalQuestion: AdditionalQuestion, expiryDate: Instant)
    fun updateResultsSent(id: Long, value: Boolean)
}
