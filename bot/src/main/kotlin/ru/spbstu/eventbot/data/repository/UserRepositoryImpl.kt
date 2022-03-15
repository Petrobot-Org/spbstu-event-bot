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
}