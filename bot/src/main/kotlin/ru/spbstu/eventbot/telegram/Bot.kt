package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.*
import ru.spbstu.eventbot.telegram.flows.ClientRegistrationFlow
import ru.spbstu.eventbot.telegram.flows.CourseCreationFlow
import ru.spbstu.eventbot.telegram.flows.RegistrationFlow

class Bot : KoinComponent {
    private val submitApplication: SubmitApplicationUseCase by inject()
    private val getAvailableCourses: GetAvailableCoursesUseCase by inject()
    private val getAvailableCoursesByClientId: GetClientCoursesUseCase by inject()
    private val getCourseById: GetCourseByIdUseCase by inject()
    private val getApplicants: GetApplicantsByCourseIdUseCase by inject()

    private val providePermissions: ProvidePermissions by inject()
    private val registrationFlow: RegistrationFlow by inject()
    private val courseCreationFlow: CourseCreationFlow by inject()
    private val clientRegistrationFlow: ClientRegistrationFlow by inject()

    private val states = mutableMapOf<Long, ChatState>()

    fun start(telegramToken: String) {
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
            "details" -> courseDetails(arg.toLong(), getCourseById)
            "applicants" -> applicantsInfo(arg.toLong(), getApplicants)
            "apply" -> TODO("Handle submit application callback")
            "newcourse" -> courseCreationFlow.onClientSelected(arg.toLong(), setState)
        }
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleText(state: ChatState, setState: (ChatState) -> Unit) {
        when (text) {
            "/register", Strings.ButtonRegister -> registrationFlow.start(setState)
            "/help" -> writeHelp()
            "/start" -> writeStart()
            "/courses", Strings.ButtonCourses -> displayCourses(getAvailableCourses)
            "/newclient", Strings.ButtonNewClient -> require(canModifyClients) {
                clientRegistrationFlow.start(setState)
            }
            "/getapplicants" -> require(canAccessAnyCourse || canAccessTheirCourse) {
                displayApplicants(getAvailableCoursesByClientId)
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
        }
    }
}
