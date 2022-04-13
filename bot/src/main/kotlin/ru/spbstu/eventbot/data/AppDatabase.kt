package ru.spbstu.eventbot.data

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import ru.spbstu.eventbot.data.adapter.*
import ru.spbstu.eventbot.data.entities.Client
import ru.spbstu.eventbot.data.entities.Course
import ru.spbstu.eventbot.data.entities.Student
import ru.spbstu.eventbot.data.source.AppDatabase
import java.sql.SQLException

fun appDatabase(jdbcString: String): AppDatabase {
    val driver = JdbcSqliteDriver(jdbcString).also {
        try {
            AppDatabase.Schema.create(it)
        } catch (e: SQLException) {
            println("Schema has already been created")
        }
    }
    return AppDatabase(
        driver = driver,
        CourseAdapter = Course.Adapter(
            expiry_dateAdapter = DateAdapter()
        ),
        ClientAdapter = Client.Adapter(
            emailAdapter = EmailAdapter(),
            nameAdapter = ClientNameAdapter()
        ),
        StudentAdapter = Student.Adapter(
            emailAdapter = EmailAdapter(),
            full_nameAdapter = FullNameAdapter(),
            group_numberAdapter = GroupAdapter()
        )
    )
}
