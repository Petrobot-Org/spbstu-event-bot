package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val database: AppDatabase) : ApplicationRepository {
    override fun insert(chatId: Long, courseId: Long) {
         database.applicationQueries.insertUsingChatId(chatId, courseId)
    }
}
