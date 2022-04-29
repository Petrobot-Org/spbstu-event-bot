package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.CourseTitle

object TitleAdapter : ColumnAdapter<CourseTitle, String> {
    override fun encode(value: CourseTitle) =
        value.value
    override fun decode(databaseValue: String): CourseTitle =
        CourseTitle.valueOf(databaseValue)!!
}
