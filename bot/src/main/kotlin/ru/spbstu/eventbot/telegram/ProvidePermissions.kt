package ru.spbstu.eventbot.telegram

import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import ru.spbstu.eventbot.domain.permissions.GetPermissionsUseCase
import ru.spbstu.eventbot.domain.permissions.Permissions

class ProvidePermissions(
    private val getPermissions: GetPermissionsUseCase
) {
    context(CallbackQueryHandlerEnvironment)
    operator fun invoke(action: context(Permissions) () -> Unit) {
        val permissions = getPermissions(
            userId = callbackQuery.from.id,
            chatId = callbackQuery.message?.chat?.id ?: return
        )
        with(permissions, action)
    }

    context(TextHandlerEnvironment)
    operator fun invoke(action: context(Permissions) () -> Unit) {
        val permissions = getPermissions(
            userId = message.from?.id ?: return,
            chatId = message.chat.id
        )
        with(permissions, action)
    }
}
