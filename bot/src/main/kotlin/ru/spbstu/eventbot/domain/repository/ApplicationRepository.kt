package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.entities.StudentId

interface ApplicationRepository {
    fun insert(studentId: StudentId, courseId: CourseId, additionalInfo: String?): Boolean
    fun contains(studentId: StudentId, courseId: CourseId): Boolean
    fun getApplications(courseId: CourseId): List<Application>
    fun delete(studentId: StudentId, courseId: CourseId)
}
