package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Course
import java.time.Instant

interface CourseRepository {
    fun getAvailable(): List<Course>
    fun getById(id: Long): Course?
    fun insert(clientId: Long, title: String, description: String, additionalQuestion: String, expiryDate: Instant)
}
