package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.Permissions

context(Permissions)
fun TextHandlerEnvironment.writeHelp() {
    val helpText = buildString {
        append(Strings.HelpCommands)
        append(Strings.StartDescription)
        append(Strings.RegisterDescription)
        append(Strings.CoursesDescription)

        if (canAccessTheirCourse || canAccessAnyCourse) {
            append(Strings.GetApplicantsDescription)
            append(Strings.NewCourseDescription)
        }
        if (canModifyClients) append(Strings.NewClientDescription)
    }
        sendReply(
            text = helpText
        )
    }