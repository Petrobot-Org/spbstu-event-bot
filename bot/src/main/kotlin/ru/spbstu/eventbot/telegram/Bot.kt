package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.telegram.flows.ClientRegistrationFlow
import ru.spbstu.eventbot.telegram.flows.CourseCreationFlow
import ru.spbstu.eventbot.telegram.flows.CoursesFlow
import ru.spbstu.eventbot.telegram.flows.RegistrationFlow

class Bot(
    private val telegramToken: String,
    private val providePermissions: ProvidePermissions,
    private val provideState: ProvideState,
    private val registrationFlow: RegistrationFlow,
    private val courseCreationFlow: CourseCreationFlow,
    private val clientRegistrationFlow: ClientRegistrationFlow,
    private val coursesFlow: CoursesFlow
) {
    val bot = bot {
        logLevel = LogLevel.Error
        token = telegramToken
        dispatch {
            callbackQuery {
                providePermissions {
                    provideState {
                        handleCallback()
                    }
                }
            }
            text {
                providePermissions {
                    provideState {
                        handleText()
                    }
                }
            }
        }
    }

    fun start() {
        bot.startPolling()
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    private fun handleCallback() {
        val tokens = callbackQuery.data.split(' ')
        require(tokens.size == 2)
        val command = tokens[0]
        val arg = tokens[1]
        when (command) {
            "details" -> coursesFlow.details(CourseId(arg.toLong()))
            "apply" -> coursesFlow.apply(CourseId(arg.toLong()))
            "revoke" -> coursesFlow.revoke(CourseId(arg.toLong()))
            "applicants" -> requirePermissions(canAccessAnyCourse || canAccessTheirCourse) {
                coursesFlow.applicantsInfo(CourseId(arg.toLong()))
            }
            "newcourse" -> requirePermissions(canAccessAnyCourse || canAccessTheirCourse) {
                courseCreationFlow.onClientSelected(ClientId(arg.toLong()))
            }
            "select_year" -> courseCreationFlow.selectYear(Year.valueOf(arg.toInt())!!)
            "unselect_year" -> courseCreationFlow.unselectYear(Year.valueOf(arg.toInt())!!)
            "select_speciality" -> courseCreationFlow.selectSpeciality(Speciality.valueOf(arg)!!)
            "unselect_speciality" -> courseCreationFlow.unselectSpeciality(Speciality.valueOf(arg)!!)
            "confirm_group_matcher" -> courseCreationFlow.confirmGroupMatcher(arg.toRegex())
        }
    }

    context(Permissions, StateEnvironment<ChatState>, TextHandlerEnvironment)
    private fun handleText() {
        when (text) {
            "/register", Strings.ButtonRegister -> registrationFlow.start()
            "/help" -> writeHelp()
            "/start" -> writeStart()
            "/courses", Strings.ButtonCourses -> coursesFlow.display()
            "/newclient", Strings.ButtonNewClient -> requirePermissions(canModifyClients) {
                clientRegistrationFlow.start()
            }
            "/getapplicants" -> requirePermissions(canAccessAnyCourse || canAccessTheirCourse) {
                coursesFlow.displayApplicants()
            }
            "/newcourse", Strings.ButtonNewCourse -> requirePermissions(canAccessAnyCourse || canAccessTheirCourse) {
                courseCreationFlow.start()
            }
            "/secret" -> sendSecret()
            else -> handleFreeText()
        }
    }

    context(Permissions, StateEnvironment<ChatState>, TextHandlerEnvironment)
    private fun handleFreeText() {
        ifState<ChatState.Empty> { sendReply(Strings.UnknownCommand) }
        ifState<ChatState.Registration> { registrationFlow.handle() }
        ifState<ChatState.ClientRegistration> { clientRegistrationFlow.handle() }
        ifState<ChatState.NewCourseCreation> { courseCreationFlow.handle() }
        ifState<ChatState.AdditionalInfoRequested> { coursesFlow.handleAdditionalInfo() }
    }
}
