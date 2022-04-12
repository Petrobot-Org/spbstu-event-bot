package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.Permissions

context(Permissions)
fun TextHandlerEnvironment.writeHelp() {
    var helpText = Strings.HelpCommands + Strings.StartDescription

    helpText += Strings.RegisterDescription
    helpText += Strings.CoursesDescription

    if (canAccessTheirCourse || canAccessAnyCourse) {
        helpText += Strings.GetApplicantsDescription
        helpText += Strings.NewCourseDescription
    }
    if (canModifyClients)  helpText+= Strings.NewClientDescription
    sendReply(
        text = helpText
        )
}