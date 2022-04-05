package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.ClientRepository

class ClientRepositoryImpl(private val database: AppDatabase) : ClientRepository {
    override fun insert(name: String, email: String, userId: Long?) {
        database.clientQueries.insert(name = name, email = email, userId = userId)
    }

    override fun contains(userId: Long): Boolean {
        return database.clientQueries.containsUserId(userId).executeAsOne() == 1L
    }
}
