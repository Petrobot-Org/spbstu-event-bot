package ru.spbstu.eventbot.domain.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

data class ExpiredCourse(
    val course: Course,
    val markAsSent: () -> Unit
)

@OptIn(ExperimentalCoroutinesApi::class)
class GetExpiredCoursesFlowUseCase(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(): Flow<ExpiredCourse> {
        return courseRepository
            .getEarliestUnsent()
            .distinctUntilChanged()
            .filterNotNull()
            .mapLatest {
                val untilExpiration = Instant.now().until(it.expiryDate, ChronoUnit.MILLIS)
                delay(untilExpiration)
                ExpiredCourse(
                    course = it,
                    markAsSent = { courseRepository.updateResultsSent(it.id, true) }
                )
            }
    }
}
