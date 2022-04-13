package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.repository.ClientRepository

class ClientRepositoryImpl(private val database: AppDatabase) : ClientRepository {
    private val map =
        { id: Long, email: String, name: String, userId: Long? ->
            Client(id, email, name, userId)
        }

    override fun insert(name: String, email: String, userId: Long?) {
        database.clientQueries.insert(name = name, email = email, userId = userId)
    }

    override fun contains(userId: Long): Boolean {
        return database.clientQueries.containsUserId(userId).executeAsOne() >= 1L
    }

    override fun getByUserId(userId: Long): List<Client> {
        return database.clientQueries.getClientsByUserId(userId, map).executeAsList()
    }

    override fun getById(id: Long): Client? {
        return database.clientQueries.getById(id, map).executeAsOneOrNull()
    }

    override fun getAll(): List<Client> {
        return database.clientQueries.getAll(map).executeAsList()
    }
}
