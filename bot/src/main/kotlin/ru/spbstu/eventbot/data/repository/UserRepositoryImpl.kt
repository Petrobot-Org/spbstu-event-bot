package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.User
import ru.spbstu.eventbot.domain.repository.UserRepository

class UserRepositoryImpl(private val database: AppDatabase) : UserRepository {
    override fun findByChatId(chatId: Long): User {
        return database.userQueries.findByChatId(chatId, mapper = { id, chatId, email, name, groupNumber ->
            User(id, chatId, email, name, groupNumber)
        }).executeAsOne()
    }

    override fun insert(chatId: Long, name: String, email: String, group: String) {
        database.userQueries.insert(chatId, email, name, group)
    }
}
