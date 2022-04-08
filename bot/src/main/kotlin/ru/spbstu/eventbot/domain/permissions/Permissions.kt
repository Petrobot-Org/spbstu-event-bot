package ru.spbstu.eventbot.domain.permissions

sealed interface Permissions {
    data class Operator(override val chatId: Long, override val userId: Long) : Permissions {
        override val canModifyClients = true
        override val canAccessAnyCourse = true
        override val canAccessTheirCourse = true
    }

    data class Client(override val chatId: Long, override val userId: Long) : Permissions {
        override val canModifyClients = false
        override val canAccessAnyCourse = false
        override val canAccessTheirCourse = true
    }

    data class Student(override val chatId: Long, override val userId: Long) : Permissions {
        override val canModifyClients = false
        override val canAccessAnyCourse = false
        override val canAccessTheirCourse = false
    }

    val chatId: Long
    val userId: Long
    val canModifyClients: Boolean
    val canAccessAnyCourse: Boolean
    val canAccessTheirCourse: Boolean
}
