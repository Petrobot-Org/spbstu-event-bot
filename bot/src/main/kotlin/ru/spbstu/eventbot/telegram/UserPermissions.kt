package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.entities.User
import java.io.File

class UserPermissions(operatorsFile: File) {
    private val operatorUserIds = operatorsFile
        .readText()
        .lines()
        .filter { it.isNotEmpty() }
        .map { it.toLong() }

    fun isOperator(user: User?): Boolean {
        return user?.id in operatorUserIds
    }
}
