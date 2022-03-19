package ru.spbstu.eventbot.data.repository

import com.squareup.sqldelight.Query
import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository

class CourseRepositoryImpl(private val database: AppDatabase) : CourseRepository {
    override fun selectAll(): List<Course> {
        return database.courseQueries
            .selectAll(mapper = {id, name -> Course(id, name)})
            .executeAsList()
    }
}