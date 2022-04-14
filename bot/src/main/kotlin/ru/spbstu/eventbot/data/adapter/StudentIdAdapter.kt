package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.StudentId

object StudentIdAdapter : ColumnAdapter<StudentId, Long> {
    override fun decode(databaseValue: Long): StudentId =
        StudentId(databaseValue)
    override fun encode(value: StudentId): Long =
        value.value
}
