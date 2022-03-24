package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.StudentRepository

class StudentRepositoryImpl(private val database: AppDatabase) : StudentRepository {
    override fun insert(chatId: Long, email: String, fullName: String, group: String) {
        database.studentQueries.insert(chatId, email, fullName, group)
    }
}
