package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Application

interface ApplicationRepository {
    fun insert(studentId: Long, courseId: Long, additionalInfo: String?)
    fun contains(studentId: Long, courseId: Long): Boolean
    fun getApplications(courseId: Long): List<Application>
    fun delete(studentId: Long, courseId: Long)
}
