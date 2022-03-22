package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.StudentRepository

class UserRepositoryImpl(private val database: AppDatabase) : StudentRepository {
    override fun findByChatId(chatId: Long): Student {
        return database.userQueries.findByChatId(chatId, mapper = { id, chatId, email, name, groupNumber ->
            Student(id, chatId, email, name, groupNumber)
        }).executeAsOne()
    }

    override fun insert(chatId: Long, email: String, name: String, group: String) {
        database.userQueries.insert(chatId, email, name, group)
    }
}
