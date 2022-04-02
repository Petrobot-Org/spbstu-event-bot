package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Application

interface ApplicationRepository {
    fun insert(studentId: Long, courseId: Long)
    fun getListOfApplicants(id:Long):List<Application>
}
