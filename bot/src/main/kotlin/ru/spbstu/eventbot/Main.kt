package ru.spbstu.eventbot

import org.koin.core.context.startKoin
import ru.spbstu.eventbot.telegram.Bot

fun main() {
    startKoin {
        modules(mainModule)
        val bot = koin.get<Bot>()
        val expiredCoursesCollector = koin.get<ExpiredCoursesCollector>()
        val newCoursesCollector = koin.get<NewCoursesCollector>()
        bot.start()
        expiredCoursesCollector.start(bot.bot)
        newCoursesCollector.start(bot.bot)
    }
}
