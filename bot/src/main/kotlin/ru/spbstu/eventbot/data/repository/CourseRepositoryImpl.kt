package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
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
          additional_question: String?,
          expiryDate: Instant?,
          resultsSent: Boolean?,
          _: Long,
          email: String,
          name: String,
          userId: Long? ->
            val client = Client(clientId, email, name, userId)
            Course(id, title, description, additional_question, client, expiryDate!!, resultsSent!!)
        }

    override fun getAvailable(): List<Course> {
        return database.courseQueries.findAvailable(Instant.now(), map).executeAsList()
    }

    override fun getAvailableCoursesByUserId(userId: Long): List<Course> {
        return database.courseQueries.getAvailableCoursesByUserId(Instant.now(), userId, map).executeAsList()
    }

    override fun getById(id: Long): Course? {
        return database.courseQueries.getById(id, map).executeAsOneOrNull()
    }

    override fun insert(
        clientId: Long,
        title: String,
        description: String,
        additionalQuestion: String,
        expiryDate: Instant
    ) {
        database.courseQueries.insert(clientId, title, description, additionalQuestion, expiryDate)
    }
}
