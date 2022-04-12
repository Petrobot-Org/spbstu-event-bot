package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val database: AppDatabase) : ApplicationRepository {
    private val map = { id: Long, chatId: Long, email: String, fullName: String, group: String ->
        Student(id, chatId, email, fullName, group)
    }

    override fun insert(studentId: Long, courseId: Long) {
        database.applicationQueries.insert(studentId, courseId)
    }

    override fun contains(studentId: Long, courseId: Long): Boolean {
        return database.applicationQueries.contains(studentId = studentId, courseId = courseId).executeAsOne() == 1L
    }

    override fun getListOfApplicants(id: Long): List<Student> {
        return database.applicationQueries.getListOfApplicants(id, map).executeAsList()
    }
}
