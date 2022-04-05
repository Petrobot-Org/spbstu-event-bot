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
import ru.spbstu.eventbot.domain.permissions.GetPermissionsUseCase
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.*

class Bot : KoinComponent {
    private val submitApplication: SubmitApplicationUseCase by inject()
    private val registerStudent: RegisterStudentUseCase by inject()
    private val getAvailableCourses: GetAvailableCoursesUseCase by inject()
    private val getCourseById: GetCourseByIdUseCase by inject()
    private val registerClient: RegisterClientUseCase by inject()
    private val getPermissions: GetPermissionsUseCase by inject()

    private val states = mutableMapOf<Long, ChatState>()

    fun start(telegramToken: String) {
        val bot = bot {
            logLevel = LogLevel.Error
            token = telegramToken
            dispatch {
                callbackQuery {
                    val (state, setState) = state(callbackQuery.message?.chat?.id ?: return@callbackQuery)
                    val permissions = getPermissions(callbackQuery.from.id)
                    with(permissions) {
                        handleCallback(state, setState)
                    }
                }
                text {
                    val (state, setState) = state(message.chat.id)
                    val permissions = getPermissions(message.from?.id)
                    with(permissions) {
                        handleCommand(state, setState)
                    }
                }
            }
        }
        bot.startPolling()
    }

    private fun state(chatId: Long): Pair<ChatState, (ChatState) -> Unit> {
        return (states[chatId] ?: ChatState.Empty) to { newState: ChatState -> states[chatId] = newState }
    }

    context(Permissions)
    private fun CallbackQueryHandlerEnvironment.handleCallback(
        state: ChatState,
        setState: (ChatState) -> Unit
    ) {
        val tokens = callbackQuery.data.split(' ')
        require(tokens.size == 2)
        val command = tokens[0]
        val arg = tokens[1]
        when (command) {
            "details" -> courseDetails(arg.toLong(), getCourseById)
            "apply" -> TODO("Handle submit application callback")
        }
    }

    context(Permissions)
    private fun TextHandlerEnvironment.handleCommand(state: ChatState, setNewState: (ChatState) -> Unit) {
        when (text) {
            "/register", Strings.ButtonRegister -> startRegistration(setNewState)
            "/help" -> writeHelp()
            "/start" -> writeStart()
            "/courses", Strings.ButtonCourses -> displayCourses(getAvailableCourses)
            "/newclient", Strings.ButtonNewClient -> require(canModifyClients) { startClientRegistration(setNewState) }
            else -> handleText(state, setNewState)
        }
    }

    context(Permissions)
    private fun TextHandlerEnvironment.handleText(state: ChatState, setNewState: (ChatState) -> Unit) {
        when (state) {
            ChatState.Empty -> sendReply(Strings.UnknownCommand)
            is ChatState.Registration -> handleRegistration(state, setNewState, registerStudent)
            is ChatState.ClientRegistration -> handleClientRegistration(state, setNewState, registerClient)
        }
    }

    private fun TextHandlerEnvironment.require(condition: Boolean, action: TextHandlerEnvironment.() -> Unit) {
        if (condition) {
            action()
        } else {
            sendReply(Strings.UnauthorizedError)
        }
    }

    private fun CallbackQueryHandlerEnvironment.require(
        condition: Boolean,
        action: CallbackQueryHandlerEnvironment.() -> Unit
    ) {
        if (condition) {
            action()
        } else {
            sendReply(Strings.UnauthorizedError)
        }
    }
}
