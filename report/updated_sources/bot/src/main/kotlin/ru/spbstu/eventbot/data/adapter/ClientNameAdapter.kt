package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.ClientName

class ClientNameAdapter : ColumnAdapter<ClientName, String> {
    override fun encode(value: ClientName) =
        value.value
    override fun decode(databaseValue: String): ClientName =
        ClientName.valueOf(databaseValue)!!
}
