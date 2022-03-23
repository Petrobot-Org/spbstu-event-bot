package ru.spbstu.eventbot.data.adapter

import com.squareup.sqldelight.ColumnAdapter
import java.util.GregorianCalendar

class DateAdapter : ColumnAdapter<GregorianCalendar, Long> {
    override fun encode(value: GregorianCalendar) = value.timeInMillis
    override fun decode(databaseValue: Long) = GregorianCalendar.getInstance().apply {
        timeInMillis = databaseValue
    } as GregorianCalendar
}
