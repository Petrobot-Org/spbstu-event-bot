package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.repository.StudentRepository

class StudentRepositoryImpl(private val database: AppDatabase) : StudentRepository {
    private val map =
        { id: StudentId, chatId: Long, email: Email, fullName: FullName, group: Group ->
            Student(id, chatId, email, fullName, group)
        }
    override fun insert(chatId: Long, email: Email, fullName: FullName, group: Group): Boolean {
        return database.transactionWithResult {
            database.studentQueries.insert(chatId, email, fullName, group)
            database.studentQueries.rowsAffected().executeAsOne() >= 1L
        }
    }

    override fun findByChatId(chatId: Long): Student? {
        return database.studentQueries.findByChatId(chatId, map).executeAsOneOrNull()
    }
}
