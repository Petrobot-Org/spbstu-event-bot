package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.repository.ClientRepository

class ClientRepositoryImpl(private val database: AppDatabase) : ClientRepository {
    private val map =
        { id: Long, userId: Long?, email: String, name: String ->
            Client(id, userId, email, name)
        }

    override fun getClientsByUserId(userId:Long):List<Client> {
        return database.clientQueries.getClientsByUserId(userId, map).executeAsList()
    }

    override fun insert(userId: Long, name: String, email: String) {
        database.clientQueries.insert(user_id = userId, name = name, email = email)
    }
}
