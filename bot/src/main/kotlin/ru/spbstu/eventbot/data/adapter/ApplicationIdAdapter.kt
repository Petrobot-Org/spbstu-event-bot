package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.ApplicationId

object ApplicationIdAdapter : ColumnAdapter<ApplicationId, Long> {
    override fun decode(databaseValue: Long): ApplicationId =
        ApplicationId(databaseValue)
    override fun encode(value: ApplicationId): Long =
        value.value
}
