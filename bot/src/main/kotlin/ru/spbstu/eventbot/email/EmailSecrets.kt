package ru.spbstu.eventbot.email

data class EmailSecrets(
    val hostname: String,
    val port: String,
    val username: String,
    val password: String,
    val from: String
)
