package ru.spbstu.eventbot

import ru.spbstu.eventbot.domain.entities.Group
import ru.spbstu.eventbot.domain.entities.Speciality
import ru.spbstu.eventbot.domain.entities.Year
import ru.spbstu.eventbot.domain.permissions.Operators
import ru.spbstu.eventbot.email.EmailSecrets
import ru.spbstu.eventbot.telegram.TelegramToken
import ru.spbstu.eventbot.telegram.flows.GroupFilters
import java.time.ZoneId
import java.util.Properties

data class AppConfig(
    val jdbcString: String,
    val operators: Operators,
    val zone: ZoneId,
    val groupFilters: GroupFilters
)

data class Secrets(
    val telegramToken: TelegramToken,
    val emailSecrets: EmailSecrets
)

fun appConfig(): AppConfig {
    return AppConfig::class.java.getResourceAsStream(
        "/application.properties"
    ).use { inputStream ->
        val properties = Properties().apply { load(inputStream) }
        val operatorUserIds = properties["operator_ids"].toString()
            .split(',')
            .filter { it.isNotEmpty() }
            .map { it.trim().toLong() }
        val jdbcString = properties["jdbc"].toString()
        val zone = ZoneId.of(properties["timezone"].toString())
        val maxYear = properties["max_year"].toString().toInt()
        val specialities = properties["specialities"].toString()
            .split(',')
            .map { Speciality.valueOf(it.trim())!! }
            .toSet()
        val numbers = properties["group_numbers"].toString()
            .split(',')
            .map { Group.valueOf(it.trim())!! }
            .toSet()
        val years = (1..maxYear).map { Year.valueOf(it)!! }
        AppConfig(
            jdbcString = jdbcString,
            operators = { it in operatorUserIds },
            zone = zone,
            groupFilters = object : GroupFilters {
                override val years = years
                override val specialities = specialities
                override val numbers = numbers
            }
        )
    }
}

fun secrets(): Secrets {
    val telegramToken = System.getenv("TELEGRAM_TOKEN")
    val smtpHostname = System.getenv("SMTP_HOSTNAME")
    val smtpPort = System.getenv("SMTP_PORT")
    val smtpUsername = System.getenv("SMTP_USERNAME")
    val smtpPassword = System.getenv("SMTP_PASSWORD")
    val smtpFrom = System.getenv("SMTP_FROM")
    return Secrets(
        telegramToken = TelegramToken(telegramToken),
        emailSecrets = EmailSecrets(
            hostname = smtpHostname,
            port = smtpPort,
            username = smtpUsername,
            password = smtpPassword,
            from = smtpFrom
        )
    )
}
