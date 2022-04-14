package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
import ru.spbstu.eventbot.domain.entities.Student

interface StudentRepository {
    fun insert(chatId: Long, email: Email, fullName: FullName, group: Group)
    fun findByChatId(chatId: Long): Student?
}
