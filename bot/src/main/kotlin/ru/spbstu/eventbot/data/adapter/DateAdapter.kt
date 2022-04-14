package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import java.time.Instant

object DateAdapter : ColumnAdapter<Instant, Long> {
    override fun encode(value: Instant) =
        value.epochSecond
    override fun decode(databaseValue: Long): Instant =
        Instant.ofEpochSecond(databaseValue)
}
