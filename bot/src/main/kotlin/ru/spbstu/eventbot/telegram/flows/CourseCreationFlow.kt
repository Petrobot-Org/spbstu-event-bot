package ru.spbstu.eventbot.telegram.flows

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.permissions.Permissions
import ru.spbstu.eventbot.domain.usecases.CreateNewCourseUseCase
import ru.spbstu.eventbot.domain.usecases.GetMyClientsUseCase
import ru.spbstu.eventbot.telegram.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

interface GroupFilters {
    val years: Collection<Year>
    val specialities: Collection<Speciality>
}

class CourseCreationFlow(
    private val getMyClients: GetMyClientsUseCase,
    private val createNewCourse: CreateNewCourseUseCase,
    private val zone: ZoneId,
    private val groupFilters: GroupFilters
) {
    context(Permissions, TextHandlerEnvironment)
    fun start() {
        when (val result = getMyClients()) {
            is GetMyClientsUseCase.Result.OK -> {
                val buttons = result.clients.map {
                    listOf(InlineKeyboardButton.CallbackData(it.name.value, "newcourse ${it.id.value}"))
                }
                sendReply(
                    text = Strings.SelectClient,
                    replyMarkup = InlineKeyboardMarkup.create(buttons)
                )
            }
            GetMyClientsUseCase.Result.Unauthorized -> sendReply(Strings.UnauthorizedError)
        }
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    fun onClientSelected(clientId: ClientId) {
        setState(ChatState.NewCourseCreation(NewCourseCreationRequest.Title, clientId))
        sendReply(Strings.RequestTitle)
    }

    context(Permissions, StateEnvironment<ChatState.NewCourseCreation>, TextHandlerEnvironment)
    fun handle() {
        val newState = when (state.request) {
            NewCourseCreationRequest.Title -> handleTitle(state)
            NewCourseCreationRequest.Description -> handleDescription(state)
            NewCourseCreationRequest.AdditionalQuestion -> handleAdditionalQuestion(state)
            NewCourseCreationRequest.ExpiryDate -> handleExpiryDate(state)
            NewCourseCreationRequest.GroupMatcher -> handleGroupMatcher(state)
            NewCourseCreationRequest.Confirm -> {
                handleConfirmation(state, setState)
                return
            }
        }
        val request = requestInfo(bot, newState)
        setState(newState.copy(request = request))
    }

    context(TextHandlerEnvironment)
    private fun handleTitle(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val title = CourseTitle.valueOf(text)
        return state.copy(title = title)
    }

    context(TextHandlerEnvironment)
    private fun handleDescription(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val description = CourseDescription.valueOf(text)
        return state.copy(description = description)
    }

    context(TextHandlerEnvironment)
    private fun handleAdditionalQuestion(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val additionalQuestion = AdditionalQuestion(if (text.lowercase() in Strings.NegativeAnswers) null else text)
        return state.copy(additionalQuestion = additionalQuestion)
    }

    context(TextHandlerEnvironment)
    private fun handleExpiryDate(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val formatter = DateTimeFormatter
            .ofPattern("dd.MM.uuuu HH:mm")
            .withZone(zone)
        val date = try {
            formatter.parse(text, Instant::from)
        } catch (e: DateTimeParseException) {
            sendReply(text = Strings.InvalidDate, parseMode = ParseMode.MARKDOWN, )
            return state
        }
        return state.copy(expiryDate = date)
    }

    context(TextHandlerEnvironment)
    private fun handleGroupMatcher(state: ChatState.NewCourseCreation): ChatState.NewCourseCreation {
        val groupMatcher = Regex(text) // TODO: Показать, какие группы подпадают по regex
        return state.copy(groupMatcher = groupMatcher)
    }

    context(Permissions, TextHandlerEnvironment)
    private fun handleConfirmation(state: ChatState.NewCourseCreation, setState: (ChatState) -> Unit) {
        when (text.lowercase()) {
            in Strings.PositiveAnswers -> {
                val result = createNewCourse(
                    state.clientId,
                    state.title!!,
                    state.description!!,
                    state.additionalQuestion!!,
                    state.expiryDate!!,
                    state.groupMatcher!!
                )
                when (result) {
                    CreateNewCourseUseCase.Result.OK -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.CreatedNewCourseSuccessfully)
                    }
                    CreateNewCourseUseCase.Result.Error -> {
                        sendReply(Strings.ErrorRetry)
                        start()
                    }
                    CreateNewCourseUseCase.Result.Unauthorized -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.UnauthorizedError)
                    }
                    CreateNewCourseUseCase.Result.NoSuchClient -> {
                        setState(ChatState.Empty)
                        sendReply(Strings.NoSuchClient)
                    }
                }
            }
            in Strings.NegativeAnswers -> {
                sendReply(Strings.ErrorRetry)
                start()
            }
            else -> {
                sendReply(Strings.RequestYesNo)
            }
        }
    }

    context(Permissions)
    private fun requestInfo(bot: Bot, state: ChatState.NewCourseCreation): NewCourseCreationRequest {
        return when {
            state.title == null -> {
                bot.sendMessage(ChatId.fromId(chatId), Strings.RequestTitle)
                NewCourseCreationRequest.Title
            }
            state.description == null -> {
                bot.sendMessage(ChatId.fromId(chatId), Strings.RequestDescription)
                NewCourseCreationRequest.Description
            }
            state.additionalQuestion == null -> {
                bot.sendMessage(ChatId.fromId(chatId), text = Strings.RequestAdditionalQuestion, parseMode = ParseMode.MARKDOWN)
                NewCourseCreationRequest.AdditionalQuestion
            }
            state.expiryDate == null -> {
                bot.sendMessage(ChatId.fromId(chatId), text = Strings.RequestExpiryDate, parseMode = ParseMode.MARKDOWN)
                NewCourseCreationRequest.ExpiryDate
            }
            state.groupMatcher == null -> {
                sendGroupMatcher(bot, state)
                NewCourseCreationRequest.GroupMatcher
            }
            else -> {
                bot.sendMessage(
                    ChatId.fromId(chatId),
                    Strings.newCourseCreationConfirmation(
                        title = state.title,
                        description = state.description,
                        additionalQuestion = state.additionalQuestion.value,
                        expiryDate = state.expiryDate,
                        groupMatcher = state.groupMatcher
                    )
                )
                NewCourseCreationRequest.Confirm
            }
        }
    }

    context(Permissions)
    private fun sendGroupMatcher(bot: Bot, state: ChatState.NewCourseCreation) {
        bot.sendMessage(
            chatId = ChatId.fromId(chatId),
            text = Strings.groupMatcher(state.groupMatchingRules.toRegex(LocalDate.now())),
            replyMarkup = groupMatcherReplyMarkup(state.groupMatchingRules)
        )
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    fun selectYear(year: Year) {
        updateGroupMatcher { it.copy(years = it.years + year) }
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    fun unselectYear(year: Year) {
        updateGroupMatcher { it.copy(years = it.years - year) }
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    fun selectSpeciality(speciality: Speciality) {
        updateGroupMatcher { it.copy(specialities = it.specialities + speciality) }
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    fun unselectSpeciality(speciality: Speciality) {
        updateGroupMatcher { it.copy(specialities = it.specialities - speciality) }
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    fun confirmGroupMatcher(regex: Regex) {
        val courseCreationState = state as? ChatState.NewCourseCreation ?: run {
            bot.editMessageText(
                chatId = ChatId.fromId(chatId),
                messageId = callbackQuery.message?.messageId,
                text = Strings.ExpiredGroupMatcher,
                replyMarkup = InlineKeyboardMarkup.create(emptyList<InlineKeyboardButton>())
            )
            return
        }
        val newState = courseCreationState.copy(groupMatcher = regex)
        val request = requestInfo(bot, newState)
        setState(newState.copy(request = request))
    }

    context(Permissions, StateEnvironment<ChatState>, CallbackQueryHandlerEnvironment)
    private fun updateGroupMatcher(modifyRules: (GroupMatchingRules) -> GroupMatchingRules) {
        val courseCreationState = state as? ChatState.NewCourseCreation ?: run {
            bot.editMessageText(
                chatId = ChatId.fromId(chatId),
                messageId = callbackQuery.message?.messageId,
                text = Strings.ExpiredGroupMatcher,
                replyMarkup = InlineKeyboardMarkup.create(emptyList<InlineKeyboardButton>())
            )
            return
        }
        val rules = modifyRules(courseCreationState.groupMatchingRules)
        setState(courseCreationState.copy(groupMatchingRules = rules))
        bot.editMessageText(
            chatId = ChatId.fromId(chatId),
            messageId = callbackQuery.message?.messageId,
            text = Strings.groupMatcher(rules.toRegex(LocalDate.now())),
            replyMarkup = groupMatcherReplyMarkup(rules)
        )
    }

    private fun groupMatcherReplyMarkup(rules: GroupMatchingRules): InlineKeyboardMarkup {
        val buttons = listOf(
            yearButtonRow(groupFilters.years, rules.years),
            specialityButtonRow(groupFilters.specialities, rules.specialities),
            listOf(
                InlineKeyboardButton.CallbackData(
                    Strings.ConfirmGroupMatcher,
                    "confirm_group_matcher ${rules.toRegex(LocalDate.now())}"
                )
            )
        )
        return InlineKeyboardMarkup.create(buttons)
    }

    private fun yearButtonRow(years: Collection<Year>, selectedYears: Set<Year>): List<InlineKeyboardButton> {
        return years.map {
            val text = Strings.studyYear(it)
            if (it in selectedYears) {
                InlineKeyboardButton.CallbackData(Strings.selectedButton(text), "unselect_year ${it.value}")
            } else {
                InlineKeyboardButton.CallbackData(text, "select_year ${it.value}")
            }
        }
    }

    private fun specialityButtonRow(
        specialities: Collection<Speciality>,
        selectedSpecialities: Set<Speciality>
    ): List<InlineKeyboardButton> {
        return specialities.map {
            if (it in selectedSpecialities) {
                InlineKeyboardButton.CallbackData(Strings.selectedButton(it.value), "unselect_speciality ${it.value}")
            } else {
                InlineKeyboardButton.CallbackData(it.value, "select_speciality ${it.value}")
            }
        }
    }
}
