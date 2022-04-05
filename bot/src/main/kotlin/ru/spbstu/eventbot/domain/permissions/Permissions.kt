package ru.spbstu.eventbot.domain.permissions

sealed interface Permissions {
    object Operator : Permissions {
        override val canModifyClients = true
        override val canModifyAnyCourse = true
        override val canModifyTheirCourse = true
    }

    object Client : Permissions {
        override val canModifyClients = false
        override val canModifyAnyCourse = false
        override val canModifyTheirCourse = true
    }

    object Student : Permissions {
        override val canModifyClients = false
        override val canModifyAnyCourse = false
        override val canModifyTheirCourse = false
    }

    val canModifyClients: Boolean
    val canModifyAnyCourse: Boolean
    val canModifyTheirCourse: Boolean
}
