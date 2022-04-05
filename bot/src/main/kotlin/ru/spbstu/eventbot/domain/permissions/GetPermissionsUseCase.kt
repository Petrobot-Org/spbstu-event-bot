package ru.spbstu.eventbot.domain.permissions

import ru.spbstu.eventbot.domain.repository.ClientRepository

fun interface Operators {
    operator fun contains(userId: Long): Boolean
}

class GetPermissionsUseCase(
    private val operators: Operators,
    private val clientRepository: ClientRepository
) {
    operator fun invoke(userId: Long?): Permissions = when {
        userId == null -> Permissions.Student
        operators.contains(userId) -> Permissions.Operator
        clientRepository.contains(userId) -> Permissions.Client
        else -> Permissions.Student
    }
}
