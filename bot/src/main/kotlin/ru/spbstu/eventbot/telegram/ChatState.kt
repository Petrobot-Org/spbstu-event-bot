package ru.spbstu.eventbot.telegram

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
        val email: String? = null,
        val userId: Long? = null
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
