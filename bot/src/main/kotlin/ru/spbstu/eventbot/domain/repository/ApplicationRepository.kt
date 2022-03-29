package ru.spbstu.eventbot.domain.repository

interface ApplicationRepository {
    fun insert(studentId: Long, courseId: Long)
}
