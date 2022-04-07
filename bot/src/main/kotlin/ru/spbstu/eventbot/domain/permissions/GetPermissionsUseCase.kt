package ru.spbstu.eventbot.domain.permissions

import ru.spbstu.eventbot.domain.repository.ClientRepository

fun interface Operators {
    operator fun contains(userId: Long): Boolean
}

class GetPermissionsUseCase(
    private val operators: Operators,
    private val clientRepository: ClientRepository
) {
    operator fun invoke(userId: Long, chatId: Long): Permissions = when {
        operators.contains(userId) -> Permissions.Operator(chatId, userId)
        clientRepository.contains(userId) -> Permissions.Client(chatId, userId)
        else -> Permissions.Student(chatId, userId)
    }
}
