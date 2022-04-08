package ru.spbstu.eventbot

import ru.spbstu.eventbot.domain.permissions.Operators
import java.time.ZoneId
import java.util.Properties

data class AppConfig(
    val jdbcString: String,
    val operators: Operators,
    val zone: ZoneId
)

fun appConfig(): AppConfig {
    return AppConfig::class.java.getResourceAsStream(
        "/application.properties"
    ).use { inputStream ->
        val properties = Properties().apply { load(inputStream) }
        val operatorUserIds = properties["operator_ids"].toString()
            .split(',')
            .filter { it.isNotEmpty() }
            .map { it.toLong() }
        val jdbcString = properties["jdbc"].toString()
        val zone = ZoneId.of(properties["timezone"].toString())
        AppConfig(
            jdbcString = jdbcString,
            operators = { it in operatorUserIds },
            zone = zone
        )
    }
}
