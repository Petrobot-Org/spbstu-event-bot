package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.eventbot.domain.entities.Group

class GroupAdapter : ColumnAdapter<Group, String> {
    override fun encode(value: Group) =
        value.value
    override fun decode(databaseValue: String): Group =
        Group.valueOf(databaseValue)!!
}
