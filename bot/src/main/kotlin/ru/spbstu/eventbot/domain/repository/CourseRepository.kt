package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Course
import java.time.Instant

interface CourseRepository {
    fun getAvailable(): List<Course>
    fun getById(id: Long): Course?
    fun insert(title: String, description: String, clientId: Long, expiryDate: Instant)
}
