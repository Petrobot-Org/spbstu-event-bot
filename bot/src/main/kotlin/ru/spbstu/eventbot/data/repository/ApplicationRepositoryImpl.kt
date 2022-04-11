package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val database: AppDatabase) : ApplicationRepository {
    override fun insert(studentId: Long, courseId: Long) {
        database.applicationQueries.insert(studentId, courseId)
    }

    override fun containsApplication(studentId: Long, courseId: Long): Boolean {
        val list = database.applicationQueries.check(student_id = studentId, course_id = courseId).executeAsList()
        return list.isNotEmpty()
    }
}
