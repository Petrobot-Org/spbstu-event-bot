package ru.spbstu.eventbot.data.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.AdditionalQuestion
import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class CourseRepositoryImpl(private val database: AppDatabase) : CourseRepository {
    private val map =
        { id: Long,
            clientId: Long,
            title: String,
            description: String,
            additionalQuestion: String?,
            expiryDate: Instant?,
            resultsSent: Boolean?,
            _: Long,
            email: String,
            name: String,
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

    override fun getEarliestUnsent(): Flow<Course> {
        return database.courseQueries.getEarliestUnsent(map).asFlow().mapToOne()
    }

    override fun getById(id: Long): Course? {
        return database.courseQueries.getById(id, map).executeAsOneOrNull()
    }

    override fun insert(
        clientId: Long,
        title: String,
        description: String,
        additionalQuestion: AdditionalQuestion,
        expiryDate: Instant
    ) {
        database.courseQueries.insert(clientId, title, description, additionalQuestion.value, expiryDate)
    }

    override fun updateResultsSent(id: Long, value: Boolean) {
        database.courseQueries.updateResultsSent(id = id, resultsSent = value)
    }
}
