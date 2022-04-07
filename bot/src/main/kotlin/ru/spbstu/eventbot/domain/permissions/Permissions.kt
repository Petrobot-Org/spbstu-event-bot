package ru.spbstu.eventbot.domain.permissions

sealed interface Permissions {
    data class Operator(override val chatId: Long, override val userId: Long) : Permissions {
        override val canModifyClients = true
        override val canModifyAnyCourse = true
        override val canModifyTheirCourse = true
    }

    data class Client(override val chatId: Long, override val userId: Long) : Permissions {
        override val canModifyClients = false
        override val canModifyAnyCourse = false
        override val canModifyTheirCourse = true
    }

    data class Student(override val chatId: Long, override val userId: Long) : Permissions {
        override val canModifyClients = false
        override val canModifyAnyCourse = false
        override val canModifyTheirCourse = false
    }

    val chatId: Long
    val userId: Long
    val canModifyClients: Boolean
    val canModifyAnyCourse: Boolean
    val canModifyTheirCourse: Boolean
}
