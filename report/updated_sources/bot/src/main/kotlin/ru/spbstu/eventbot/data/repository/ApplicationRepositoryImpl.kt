package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val database: AppDatabase) : ApplicationRepository {
    private val map = { id: Long,
        courseId: Long,
        additionalInfo: String?,
        studentId: Long,
        chatId: Long,
        email: Email,
        fullName: FullName,
        group: Group ->
        val student = Student(studentId, chatId, email, fullName, group)
        Application(id, student, courseId, additionalInfo)
    }

    override fun insert(studentId: Long, courseId: Long, additionalInfo: String?) {
        database.applicationQueries.insert(studentId, courseId, additionalInfo)
    }

    override fun contains(studentId: Long, courseId: Long): Boolean {
        return database.applicationQueries.contains(studentId = studentId, courseId = courseId).executeAsOne() >= 1L
    }

    override fun getApplications(courseId: Long): List<Application> {
        return database.applicationQueries.getApplicants(courseId, map).executeAsList()
    }

    override fun delete(studentId: Long, courseId: Long) {
        database.applicationQueries.delete(studentId = studentId, courseId = courseId)
    }
}
