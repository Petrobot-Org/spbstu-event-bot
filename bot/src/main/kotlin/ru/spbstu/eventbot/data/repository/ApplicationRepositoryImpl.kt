package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val database: AppDatabase) : ApplicationRepository {
    private val map = { id: ApplicationId,
        courseId: CourseId,
        additionalInfo: String?,
        studentId: StudentId,
        chatId: Long,
        email: Email,
        fullName: FullName,
        group: Group ->
        val student = Student(studentId, chatId, email, fullName, group)
        Application(id, student, courseId, additionalInfo)
    }

    override fun insert(studentId: StudentId, courseId: CourseId, additionalInfo: String?): Boolean {
        return database.transactionWithResult {
            database.applicationQueries.insert(studentId, courseId, additionalInfo)
            database.applicationQueries.rowsAffected().executeAsOne() >= 1L
        }
    }

    override fun contains(studentId: StudentId, courseId: CourseId): Boolean {
        return database.applicationQueries.contains(studentId = studentId, courseId = courseId).executeAsOne() >= 1L
    }

    override fun getApplications(courseId: CourseId): List<Application> {
        return database.applicationQueries.getApplicants(courseId, map).executeAsList()
    }

    override fun delete(studentId: StudentId, courseId: CourseId) {
        database.applicationQueries.delete(studentId = studentId, courseId = courseId)
    }
}
