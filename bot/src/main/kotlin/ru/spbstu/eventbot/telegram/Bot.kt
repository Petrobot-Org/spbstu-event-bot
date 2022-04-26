package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel
import ru.spbstu.eventbot.domain.entities.ClientId
import ru.spbstu.eventbot.domain.entities.CourseId
import ru.spbstu.eventbot.domain.entities.GroupMatchingRules
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.telegram.flows.ClientRegistrationFlow
import ru.spbstu.eventbot.telegram.flows.CourseCreationFlow
import ru.spbstu.eventbot.telegram.flows.CoursesFlow
import ru.spbstu.eventbot.telegram.flows.RegistrationFlow

class Bot(
    private val telegramToken: String,
    private val providePermissions: ProvidePermissions,
    private val registrationFlow: RegistrationFlow,
    private val courseCreationFlow: CourseCreationFlow,
    private val clientRegistrationFlow: ClientRegistrationFlow,
    private val coursesFlow: CoursesFlow
) {
    private val states = mutableMapOf<Long, ChatState>()

    val bot = bot {
        logLevel = LogLevel.Error
        token = telegramToken
        dispatch {
            callbackQuery {
                providePermissions {
                    val (state, setState) = state()
                    handleCallback(state, setState)
                }
            }
            text {
                providePermissions {
                    val (state, setState) = state()
                    handleText(state, setState)
                }
            }
        }
    }

    fun start() {
        bot.startPolling()
    }

    context(Permissions)
    private fun state(): Pair<ChatState, (ChatState) -> Unit> {
        return (states[chatId] ?: ChatState.Empty) to { newState: ChatState -> states[chatId] = newState }
    }

    context(Permissions, CallbackQueryHandlerEnvironment)
    private fun handleCallback(state: ChatState, setState: (ChatState) -> Unit) {
        val tokens = callbackQuery.data.split(' ')
        require(tokens.size == 2)
        val command = tokens[0]
        val arg = tokens[1]
        when (command) {
            "details" -> coursesFlow.details(CourseId(arg.toLong()))
            "apply" -> coursesFlow.apply(CourseId(arg.toLong()), setState)
            "revoke" -> coursesFlow.revoke(CourseId(arg.toLong()))
            "applicants" -> require(canAccessAnyCourse || canAccessTheirCourse) {
                coursesFlow.applicantsInfo(CourseId(arg.toLong()))
            }
            "newcourse" -> require(canAccessAnyCourse || canAccessTheirCourse) {
                courseCreationFlow.onClientSelected(ClientId(arg.toLong()), setState)
            }
            "select_year" -> courseCreationFlow.selectYear(GroupMatchingRules.Year.valueOf(arg.toInt())!!, state, setState)
            "unselect_year" -> courseCreationFlow.unselectYear(GroupMatchingRules.Year.valueOf(arg.toInt())!!, state, setState)
            "select_speciality" -> courseCreationFlow.selectSpeciality(GroupMatchingRules.Speciality.valueOf(arg)!!, state, setState)
            "unselect_speciality" -> courseCreationFlow.unselectSpeciality(GroupMatchingRules.Speciality.valueOf(arg)!!, state, setState)
            "confirm_group_matcher" -> courseCreationFlow.confirmGroupMatcher(arg.toRegex(), state, setState)
        }
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleText(state: ChatState, setState: (ChatState) -> Unit) {
        when (text) {
            "/register", Strings.ButtonRegister -> registrationFlow.start(setState)
            "/help" -> writeHelp()
            "/start" -> writeStart()
            "/courses", Strings.ButtonCourses -> coursesFlow.display()
            "/newclient", Strings.ButtonNewClient -> require(canModifyClients) {
                clientRegistrationFlow.start(setState)
            }
            "/getapplicants" -> require(canAccessAnyCourse || canAccessTheirCourse) {
                coursesFlow.displayApplicants()
            }
            "/newcourse", Strings.ButtonNewCourse -> require(canAccessAnyCourse || canAccessTheirCourse) {
                courseCreationFlow.start()
            }
            else -> handleFreeText(state, setState)
        }
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleFreeText(state: ChatState, setState: (ChatState) -> Unit) {
        when (state) {
            ChatState.Empty -> sendReply(Strings.UnknownCommand)
            is ChatState.Registration -> registrationFlow.handle(state, setState)
            is ChatState.ClientRegistration -> clientRegistrationFlow.handle(state, setState)
            is ChatState.NewCourseCreation -> courseCreationFlow.handle(state, setState)
            is ChatState.AdditionalInfoRequested -> coursesFlow.handleAdditionalInfo(state, setState)
        }
    }
}
