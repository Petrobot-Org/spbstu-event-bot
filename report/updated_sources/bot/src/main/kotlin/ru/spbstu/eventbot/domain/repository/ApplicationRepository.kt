package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Student

interface ApplicationRepository {
    fun insert(studentId: Long, courseId: Long)
    fun getListOfApplicants(id: Long): List<Student>
}
