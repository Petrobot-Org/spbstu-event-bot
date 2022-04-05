package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Course

interface CourseRepository {
    fun getAvailable(): List<Course>
    fun getById(id: Long): Course?
    fun insert(studentId: Long, courseId: Long)
}
