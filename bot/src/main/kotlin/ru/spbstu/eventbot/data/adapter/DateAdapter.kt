package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class DateAdapter : ColumnAdapter<LocalDateTime, Long> {
    override fun encode(value: LocalDateTime) =
        value.toEpochSecond(ZoneOffset.UTC)
    override fun decode(databaseValue: Long): LocalDateTime =
        LocalDateTime.ofEpochSecond(databaseValue, 0, ZoneOffset.UTC)
}
