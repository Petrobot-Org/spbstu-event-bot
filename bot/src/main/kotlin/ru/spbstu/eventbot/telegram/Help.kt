package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.telegram.Strings.HelpCommands
import ru.spbstu.eventbot.telegram.Strings.StartDescription

context(Permissions)
fun TextHandlerEnvironment.writeHelp() {
    var helpText = HelpCommands + StartDescription

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