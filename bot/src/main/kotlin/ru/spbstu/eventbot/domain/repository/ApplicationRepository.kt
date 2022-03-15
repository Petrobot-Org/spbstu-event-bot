package ru.spbstu.eventbot.domain.repository

import ru.spbstu.eventbot.domain.entities.Application

interface ApplicationRepository {
    fun insert(application: Application)
}