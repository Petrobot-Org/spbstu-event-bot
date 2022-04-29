package ru.spbstu.eventbot.data

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import mu.KotlinLogging
import ru.spbstu.eventbot.data.adapter.*
import ru.spbstu.eventbot.data.entities.Application
import ru.spbstu.eventbot.data.entities.Client
import ru.spbstu.eventbot.data.entities.Course
import ru.spbstu.eventbot.data.entities.Student
import ru.spbstu.eventbot.data.source.AppDatabase
import java.sql.SQLException
import java.util.*

private val logger = KotlinLogging.logger { }

fun createAppDatabase(jdbcString: String): AppDatabase {
    val driver = JdbcSqliteDriver(
        jdbcString,
        Properties(1).apply { put("foreign_keys", "true") }
    ).also {
        try {
            AppDatabase.Schema.create(it)
        } catch (e: SQLException) {
            logger.info { "Schema has already been created" }
        }
    }
    return AppDatabase(
        driver = driver,
        CourseAdapter = Course.Adapter(
            expiry_dateAdapter = DateAdapter,
            idAdapter = CourseIdAdapter,
            client_idAdapter = ClientIdAdapter,
            titleAdapter = TitleAdapter,
            descriptionAdapter = DescriptionAdapter
        ),
        ClientAdapter = Client.Adapter(
            emailAdapter = EmailAdapter,
            nameAdapter = ClientNameAdapter,
            idAdapter = ClientIdAdapter
        ),
        StudentAdapter = Student.Adapter(
            emailAdapter = EmailAdapter,
            full_nameAdapter = FullNameAdapter,
            group_numberAdapter = GroupAdapter,
            idAdapter = StudentIdAdapter
        ),
        ApplicationAdapter = Application.Adapter(
            idAdapter = ApplicationIdAdapter,
            student_idAdapter = StudentIdAdapter,
            course_idAdapter = CourseIdAdapter
        )
    )
}
