package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class FakeCourseRepositoryImpl : CourseRepository {
    private val fakeCourses = listOf(
        Course(1, "Курс от Dell", "Описание.", "",1, Instant.now(), false),
        Course(2, "Курс лично от Петрова", "Описание.", "",1, Instant.now(), false),
        Course(3, "Курс лично от Петрова", "Описание.", "",1, Instant.now().plusSeconds(100000), false),
        Course(4, "Курс лично от Петрова", "Описание.", "",1, Instant.now().plusSeconds(100000), false),
    )

    override fun getAvailable(): List<Course> {
        return fakeCourses
    }

    override fun getById(id: Long): Course? {
        return fakeCourses.find { it.id == id }
    }

    override fun insert(title: String, description: String, clientId: Long, expiryDate: Instant) {
        TODO("Not yet implemented")
    }
}
