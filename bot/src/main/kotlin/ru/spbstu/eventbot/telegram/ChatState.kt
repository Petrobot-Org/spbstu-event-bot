package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.*
import java.time.Instant

sealed interface ChatState {
    object Empty : ChatState
    data class Registration(
        val request: RegistrationRequest,
        val fullName: FullName? = null,
        val email: Email? = null,
        val group: Group? = null
    ) : ChatState

    data class ClientRegistration(
        val request: ClientRegistrationRequest,
        val name: ClientName? = null,
        val email: Email? = null,
        val userId: Long? = null
    ) : ChatState

    data class NewCourseCreation(
        val request: NewCourseCreationRequest,
        val clientId: Long,
        val title: String? = null,
        val description: String? = null,
        val additionalQuestion: AdditionalQuestion? = null,
        val expiryDate: Instant? = null
    ) : ChatState

    data class AdditionalInfoRequested(
        val courseId: Long,
        val messageId: Long?
    ) : ChatState
}

sealed interface RegistrationRequest {
    object FullName : RegistrationRequest
    object Email : RegistrationRequest
    object Group : RegistrationRequest
    object Confirm : RegistrationRequest
}

sealed interface ClientRegistrationRequest {
    object Name : ClientRegistrationRequest
    object Email : ClientRegistrationRequest
    object Confirm : ClientRegistrationRequest
    object UserId : ClientRegistrationRequest
}

sealed interface NewCourseCreationRequest {
    object Title : NewCourseCreationRequest
    object Description : NewCourseCreationRequest
    object AdditionalQuestion : NewCourseCreationRequest
    object ExpiryDate : NewCourseCreationRequest
    object Confirm : NewCourseCreationRequest
}
