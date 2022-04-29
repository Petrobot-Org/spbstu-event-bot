package ru.spbstu.eventbot.domain.usecases

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.repository.ClientRepository
import ru.spbstu.eventbot.domain.repository.CourseRepository
import java.time.Instant

class CreateNewCourseUseCase(
    private val courseRepository: CourseRepository,
    private val clientRepository: ClientRepository
) {
    sealed interface Result {
        object OK : Result
        object NoSuchClient : Result
        object Unauthorized : Result
        object Error : Result
    }

    private val _newCoursesFlow = MutableSharedFlow<Course>(replay = 1)
    val newCoursesFlow = _newCoursesFlow.asSharedFlow()

    context(Permissions)
    operator fun invoke(
        clientId: ClientId,
        title: CourseTitle,
        description: CourseDescription,
        additionalQuestion: AdditionalQuestion,
        expiryDate: Instant,
        groupMatcher: Regex
    ): Result {
        val client = clientRepository.getById(clientId) ?: return Result.NoSuchClient
        val isPermitted = canAccessAnyCourse || (canAccessTheirCourse && client.userId == userId)
        if (!isPermitted) {
            return Result.Unauthorized
        }
        val courseId = courseRepository.insert(clientId, title, description, additionalQuestion, expiryDate, groupMatcher) ?: return Result.Error
        courseRepository.getById(courseId)?.let { _newCoursesFlow.tryEmit(it) }
        return Result.OK
    }
}
