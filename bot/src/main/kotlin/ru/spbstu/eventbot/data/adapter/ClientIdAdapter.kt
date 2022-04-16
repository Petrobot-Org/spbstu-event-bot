package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.ClientId

object ClientIdAdapter : ColumnAdapter<ClientId, Long> {
    override fun decode(databaseValue: Long): ClientId =
        ClientId(databaseValue)
    override fun encode(value: ClientId): Long =
        value.value
}
