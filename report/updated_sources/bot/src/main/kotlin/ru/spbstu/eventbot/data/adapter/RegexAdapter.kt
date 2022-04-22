package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter

object RegexAdapter : ColumnAdapter<Regex, String> {
    override fun decode(databaseValue: String): Regex =
        Regex(databaseValue)
    override fun encode(value: Regex): String =
        value.pattern
}
