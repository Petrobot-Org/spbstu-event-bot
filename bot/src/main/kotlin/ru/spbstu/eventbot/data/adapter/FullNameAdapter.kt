package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import java.time.Instant

class FullNameAdapter : ColumnAdapter<FullName, String> {
    override fun encode(value: FullName) =
        value.value
    override fun decode(databaseValue: String): FullName =
        FullName.valueOf(databaseValue)!!
}
