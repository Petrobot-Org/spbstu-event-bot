package ru.spbstu.eventbot.data.adapter

///import app.cash.sqldelight.ColumnAdapter
import com.squareup.sqldelight.ColumnAdapter
import java.util.GregorianCalendar

typealias Date = GregorianCalendar

class DateAdapter constructor() : ColumnAdapter<Date, Long> {
    override fun encode(value: Date) = value.timeInMillis
    override fun decode(databaseValue: Long) = Date.getInstance().apply {
        timeInMillis = databaseValue
    } as Date
}
