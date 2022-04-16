package ru.spbstu.eventbot

import ru.spbstu.eventbot.domain.permissions.Operators
import ru.spbstu.eventbot.email.EmailSecrets
import ru.spbstu.eventbot.email.EmailSender
import java.time.ZoneId
import java.util.Properties

data class AppConfig(
    val jdbcString: String,
    val operators: Operators,
    val zone: ZoneId
)

data class Secrets(
    val telegramToken: String,
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

fun secrets(): Secrets {
    val telegramToken = System.getenv("TELEGRAM_TOKEN")
    val smtpHostname = System.getenv("SMTP_HOSTNAME")
    val smtpPort = System.getenv("SMTP_PORT")
    val smtpUsername = System.getenv("SMTP_USERNAME")
    val smtpPassword = System.getenv("SMTP_PASSWORD")
    val smtpFrom = System.getenv("SMTP_FROM")
    return Secrets(
        telegramToken = telegramToken,
        emailSecrets = EmailSecrets(
            hostname = smtpHostname,
            port = smtpPort,
            username = smtpUsername,
            password = smtpPassword,
            from = smtpFrom
        )
    )
}
