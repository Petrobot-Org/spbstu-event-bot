package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.entities.User

fun interface Operators {
    operator fun contains(user: User?): Boolean
}
