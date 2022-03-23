package ru.spbstu.eventbot.telegram

sealed interface ChatState {
    object Empty : ChatState
    data class Registration(
        val request: RegistrationRequest,
        val fullName: String? = null,
        val email: String? = null,
        val group: String? = null
    ) : ChatState
}

sealed interface RegistrationRequest {
    object FullName : RegistrationRequest
    object Email : RegistrationRequest
    object Group : RegistrationRequest
    object Confirm : RegistrationRequest
}
