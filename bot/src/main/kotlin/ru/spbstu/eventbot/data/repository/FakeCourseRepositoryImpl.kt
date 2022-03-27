package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.util.*

class FakeCourseRepositoryImpl : CourseRepository {
    private val fakeCourses = listOf(
        Course(1, "Курс от Dell", "Описание.", 1, GregorianCalendar(2022, 5, 5), false),
        Course(2, "Курс лично от Петрова", "Описание.", 1, GregorianCalendar(2022, 5, 5), false),
        Course(3, "Курс лично от Петрова", "Описание.", 1, GregorianCalendar(2022, 5, 5), false),
        Course(4, "Курс лично от Петрова", "Описание.", 1, GregorianCalendar(2022, 5, 5), false),
    )

    override fun getAvailable(): List<Course> {
        return fakeCourses
    }

    override fun getById(id: Long): Course? {
        return fakeCourses.find { it.id == id }
    }
}
