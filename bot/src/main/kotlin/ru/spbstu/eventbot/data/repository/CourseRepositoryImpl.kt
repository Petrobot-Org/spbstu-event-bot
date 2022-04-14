package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class CourseRepositoryImpl(private val database: AppDatabase) : CourseRepository {
    private val map =
        { id: CourseId,
            clientId: ClientId,
            title: CourseTitle,
            description: CourseDescription,
            additionalQuestion: String?,
            expiryDate: Instant?,
            resultsSent: Boolean?,
            _: ClientId,
            email: Email,
            name: ClientName,
            userId: Long? ->
            val client = Client(clientId, email, name, userId)
            Course(id, title, description, AdditionalQuestion(additionalQuestion), client, expiryDate!!, resultsSent!!)
        }

    override fun getAvailable(): List<Course> {
        return database.courseQueries.findAvailable(Instant.now(), map).executeAsList()
    }

    override fun getAvailableCoursesByUserId(userId: Long): List<Course> {
        return database.courseQueries.getAvailableCoursesByUserId(Instant.now(), userId, map).executeAsList()
    }

    override fun getById(id: CourseId): Course? {
        return database.courseQueries.getById(id, map).executeAsOneOrNull()
    }

    override fun insert(
        clientId: ClientId,
        title: CourseTitle,
        description: CourseDescription,
        additionalQuestion: AdditionalQuestion,
        expiryDate: Instant
    ) {
        database.courseQueries.insert(clientId, title, description, additionalQuestion.value, expiryDate)
    }
}
