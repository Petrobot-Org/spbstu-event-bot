package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.Course
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Strings {
    const val InvalidName = "Некорректное имя. Попробуйте снова."
    const val InvalidEmail = "Некорректная почта. Попробуйте снова."
    const val InvalidGroup = "Некорректная группа. Попробуйте снова."
    const val RequestName = "Давайте познакомимся. Как вас зовут?"
    const val RequestEmail = "Теперь нужен адрес электронной почты."
    const val RequestGroup = "Номер группы"
    const val RegistrationRetry = "Тогда начинаем заново"
    const val RegistrationErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val RequestYesNo = "Напишите да или нет"
    const val RegisteredSuccessfully = "Успешная регистрация"

    const val RequestClientName = "Имя заказчика"
    const val RequestClientEmail = "Адрес электронной почты заказчика"
    const val RequestClientUserId = "Id телеграма заказчика (напишите \"нет\", если нет)"
    const val ClientRegistrationRetry = "Тогда начинаем заново"
    const val ClientRegistrationErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val ClientRegisteredSuccessfully = "Клиент создан"
    const val InvalidClientName = "Некорректное имя заказчика. Попробуйте снова."
    const val InvalidClientUserId = "Id заказчика должно быть целым числом. Попробуйте снова."

    const val UnknownCommand = "Неизвестная команда"
    const val UnauthorizedError = "Недостаточно прав"
    const val AvailableCoursesHeader = "Доступные курсы"
    const val NoSuchCourse = "Этого курса не существует"
    const val SubmitApplication = "✅ Записаться"
    val PositiveAnswers = setOf("да", "ага", "угу", "д", "yes", "ye", "yeah", "y")
    val NegativeAnswers = setOf("no", "net", "n", "нет", "не", "н")

    const val HelpCommands = "I help you!"
    const val HelpStart = "I help you for start work with me!"

    const val ButtonCourses = "Курсы"
    const val ButtonRegister = "Регистрация"
    const val ButtonNewClient = "Новый заказчик"

    fun registrationConfirmation(name: String, email: String, group: String) =
        """|Имя: $name
           |Почта: $email
           |Группа: $group
           |Верно?
        """.trimMargin()

    fun clientRegistrationConfirmation(name: String, email: String, userId: Long?) =
        """|Имя: $name
           |Почта: $email
           |id: $userId
           |Верно?
        """.trimMargin()

    fun courseDetails(course: Course): String {
        val formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.LONG)
            .withZone(ZoneId.systemDefault())
        return """|*${course.title}*
                  |
                  |🕒 До ${formatter.format(course.expiryDate)}
                  |${course.description}
        """.trimMargin()
    }
}
