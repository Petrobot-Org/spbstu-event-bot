package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.StudentRepository

class StudentRepositoryImpl(private val database: AppDatabase) : StudentRepository {
    private val map =
        { id: Long, chatId: Long, email: String, fullName: String, group: String ->
            Student(id, chatId, email, fullName, group)
        }
    override fun insert(chatId: Long, email: String, fullName: String, group: String) {
        database.studentQueries.insert(chatId, email, fullName, group)
    }

    override fun findByChatId(chatId: Long): Student? {
        return database.studentQueries.findByChatId(chatId, map).executeAsOneOrNull()
    }

    override fun findById(id: Long): Student?{
        return database.studentQueries.findById(id, map).executeAsOneOrNull()
    }

}
