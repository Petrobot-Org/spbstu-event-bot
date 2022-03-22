package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository

class CourseRepositoryImpl(private val database: AppDatabase) : CourseRepository {
    override fun findByName(name: String): Course {
        return database.courseQueries.findByName(name, mapper = { id, name, clientId, expiry_date ->
            Course(id, name, clientId, expiry_date)
        }).executeAsOne()
    }
}