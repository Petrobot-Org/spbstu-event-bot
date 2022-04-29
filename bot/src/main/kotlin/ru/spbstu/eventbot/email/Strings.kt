package ru.spbstu.eventbot.email

import ru.spbstu.eventbot.domain.entities.Course

object Strings {
    const val ApplicantsTableDescription = "Заявки"

    fun courseExpiredSubject(course: Course) =
        """Сбор заявок на курс "${course.title}" завершён"""

    fun courseExpiredMessage(course: Course) =
        """Список заявок на "${course.title}" приложен"""

    fun applicantsTableFilename(course: Course) = course.title.value
        .filter { it.isLetterOrDigit() || it.isWhitespace() }
}
