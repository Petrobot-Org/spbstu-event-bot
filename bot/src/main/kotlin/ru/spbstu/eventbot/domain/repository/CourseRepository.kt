package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Course


interface CourseRepository {
    fun selectAll() : List<Course>
}