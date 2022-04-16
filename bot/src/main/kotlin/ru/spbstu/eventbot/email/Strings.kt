package ru.spbstu.eventbot.email

import ru.spbstu.eventbot.domain.entities.Course

fun subject(course: Course) =
    """время записи на курс "${course.title}" истекло""".trimMargin()
fun message(course: Course) =
    """время записи на курс "${course.title}" истекло""".trimMargin()