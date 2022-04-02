package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.repository.ClientRepository

class ClientRepositoryImpl(private val database: AppDatabase) : ClientRepository {
    override fun insert(name: String, email: String) {
        database.clientQueries.insert(name = name, email = email)
    }

    override fun findByChatId(chatId: Long): Client? {
        return database.clientQueries.findByChatId(chatId, mapper = { id, email, name ->
            Client(id, email, name)
        }).executeAsOneOrNull()
    }
}
