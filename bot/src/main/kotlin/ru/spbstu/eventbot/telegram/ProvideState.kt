package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.permissions.Permissions

data class StateEnvironment<out T : ChatState>(
    val state: T,
    val setState: (ChatState) -> Unit
)

class ProvideState {
    private val states = mutableMapOf<Long, ChatState>()

    context(Permissions)
    operator fun invoke(action: context(StateEnvironment<ChatState>) () -> Unit) {
        val stateEnvironment = StateEnvironment(
            state = states[chatId] ?: ChatState.Empty,
            setState = { states[chatId] = it }
        )
        with(stateEnvironment, action)
    }
}

context(StateEnvironment<ChatState>)
inline fun <reified S : ChatState> ifState(action: (StateEnvironment<S>).() -> Unit) {
    if (state is S) {
        with(this@StateEnvironment as StateEnvironment<S>, action)
    }
}
