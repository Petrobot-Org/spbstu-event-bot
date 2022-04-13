package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
import ru.spbstu.eventbot.domain.entities.Student
import ru.spbstu.eventbot.domain.repository.StudentRepository

class StudentRepositoryImpl(private val database: AppDatabase) : StudentRepository {
    private val map =
        { id: Long, chatId: Long, email: Email, fullName: FullName, group: Group ->
            Student(id, chatId, email, fullName, group)
        }
    override fun insert(chatId: Long, email: Email, fullName: FullName, group: Group) {
        database.studentQueries.insert(chatId, email, fullName, group)
    }

    override fun findByChatId(chatId: Long): Student? {
        return database.studentQueries.findByChatId(chatId, map).executeAsOneOrNull()
    }
}
