package ru.spbstu.eventbot.data.repository

import ru.spbstu.eventbot.data.source.AppDatabase
import ru.spbstu.eventbot.domain.entities.Application
import ru.spbstu.eventbot.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(private val database: AppDatabase) : ApplicationRepository {

    private val map = { id: Long, studentId: Long, courseId: Long, additionalInfo: String?->
        Application(id, studentId, courseId, additionalInfo)
    }
    override fun insert(studentId: Long, courseId: Long) {
        database.applicationQueries.insert(studentId, courseId)
    }
    override fun getListOfApplicants(id:Long): List<Application>{
        return database.applicationQueries.getListOfApplicants(id, map).executeAsList()
    }
}
