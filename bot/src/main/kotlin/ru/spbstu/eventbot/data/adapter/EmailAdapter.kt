package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.Email

object EmailAdapter : ColumnAdapter<Email, String> {
    override fun encode(value: Email) =
        value.value
    override fun decode(databaseValue: String): Email =
        Email.valueOf(databaseValue)!!
}
