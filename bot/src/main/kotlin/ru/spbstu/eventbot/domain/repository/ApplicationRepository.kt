package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Student

interface ApplicationRepository {
    fun insert(studentId: Long, courseId: Long)
    fun contains(studentId: Long, courseId: Long): Boolean
    fun getListOfApplicants(id: Long): List<Student>
    fun delete(studentId: Long, courseId: Long)
}
