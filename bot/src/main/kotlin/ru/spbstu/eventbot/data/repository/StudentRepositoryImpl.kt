package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.StudentRepository

class StudentRepositoryImpl(private val database: AppDatabase) : StudentRepository {
    override fun insert(chatId: Long, email: String, fullName: String, group: String) {
        database.studentQueries.insert(chatId, email, fullName, group)
    }

    override fun findByChatId(chatId: Long): Student? {
        return database.studentQueries.findByChatId(chatId, mapper = { id, chatId, email, fullName, group ->
            Student(id, chatId, email, fullName, group)
        }).executeAsOneOrNull()
    }
}
