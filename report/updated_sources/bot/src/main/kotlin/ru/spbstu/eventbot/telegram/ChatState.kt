package ru.spbstu.eventbot.telegram

import java.time.Instant

sealed interface ChatState {
    object Empty : ChatState
    data class Registration(
        val request: RegistrationRequest,
        val fullName: String? = null,
        val email: String? = null,
        val group: String? = null
    ) : ChatState

    data class ClientRegistration(
        val request: ClientRegistrationRequest,
        val name: String? = null,
        val email: String? = null
    ) : ChatState

    data class NewCourseCreation(
        val request: NewCourseCreationRequest,
        val title: String? = null,
        val description: String? = null,
        val additionalQuestion: String? = null,
        val expiryDate: Instant? = null
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
}

sealed interface NewCourseCreationRequest {
    object Title : NewCourseCreationRequest
    object Description : NewCourseCreationRequest
    object AdditionalQuestion : NewCourseCreationRequest
    object ExpiryDate : NewCourseCreationRequest
    object Confirm : NewCourseCreationRequest
}
