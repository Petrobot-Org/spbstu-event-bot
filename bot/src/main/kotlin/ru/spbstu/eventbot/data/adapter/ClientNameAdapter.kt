package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.ClientName
import ru.spbstu.eventbot.domain.entities.Email
import ru.spbstu.eventbot.domain.entities.FullName
import ru.spbstu.eventbot.domain.entities.Group
import java.time.Instant

class ClientNameAdapter : ColumnAdapter<ClientName, String> {
    override fun encode(value: ClientName) =
        value.value
    override fun decode(databaseValue: String): ClientName =
        ClientName.valueOf(databaseValue)!!
}
