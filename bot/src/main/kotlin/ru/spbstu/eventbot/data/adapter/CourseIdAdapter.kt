package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.CourseId

object CourseIdAdapter : ColumnAdapter<CourseId, Long> {
    override fun decode(databaseValue: Long): CourseId =
        CourseId(databaseValue)
    override fun encode(value: CourseId): Long =
        value.value
}
