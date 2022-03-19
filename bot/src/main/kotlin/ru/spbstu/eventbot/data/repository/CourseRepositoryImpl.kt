package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository

class CourseRepositoryImpl(private val database: AppDatabase) : CourseRepository {
    override fun selectAll(): List<Course> {
        return database.courseQueries
            .selectAll(mapper = { id, name -> Course(id, name) })
            .executeAsList()
    }

    override fun findById(idS: Long): Course {
        return database.courseQueries
            .findById(idS, mapper = { id, name -> Course(id, name) })
            .executeAsOne()
    }

    override fun insert(course: Course) {
        database.courseQueries.insert(course.id, course.name)
    }

}