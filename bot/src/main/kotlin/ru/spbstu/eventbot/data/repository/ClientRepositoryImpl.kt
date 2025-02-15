package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.entities.ClientId
import ru.spbstu.eventbot.domain.entities.ClientName
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.repository.ClientRepository

class ClientRepositoryImpl(private val database: AppDatabase) : ClientRepository {
    private val map =
        { id: ClientId, email: Email, name: ClientName, userId: Long ->
            Client(id, email, name, userId)
        }

    override fun insert(name: ClientName, email: Email, userId: Long): Boolean {
        return database.transactionWithResult {
            database.clientQueries.insert(name = name, email = email, userId = userId)
            database.clientQueries.rowsAffected().executeAsOne() >= 1L
        }
    }

    override fun contains(userId: Long): Boolean {
        return database.clientQueries.containsUserId(userId).executeAsOne() >= 1L
    }

    override fun getByUserId(userId: Long): List<Client> {
        return database.clientQueries.getClientsByUserId(userId, map).executeAsList()
    }

    override fun getById(id: ClientId): Client? {
        return database.clientQueries.getById(id, map).executeAsOneOrNull()
    }

    override fun getAll(): List<Client> {
        return database.clientQueries.getAll(map).executeAsList()
    }
}
