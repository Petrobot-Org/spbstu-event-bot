package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.CourseDescription

object DescriptionAdapter : ColumnAdapter<CourseDescription, String> {
    override fun encode(value: CourseDescription) =
        value.value
    override fun decode(databaseValue: String): CourseDescription =
        CourseDescription.valueOf(databaseValue)!!
}
