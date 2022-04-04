package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.Client
import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object Strings {
    private val  submitApplicationUseCase : SubmitApplicationUseCase = TODO()
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
    const val DontKnowWhatToDo = "Не знаю, что с этим делать"
    const val UnknownCommand = "Неизвестная команда"
    const val AvailableCoursesHeader = "Доступные курсы"
    const val NoSuchCourse = "Этого курса не существует"
    const val SubmitApplication = "✅ Записаться"
    val PositiveAnswers = setOf("да", "ага", "угу", "д", "yes", "ye", "yeah", "y")
    val NegativeAnswers = setOf("no", "net", "n", "нет", "не", "н")

    const val HelpCommands = "I help you!"
    const val HelpStart = "I help you for start work with me!"

    fun registrationConfirmation(name: String, email: String, group: String) =
        """|Имя: $name
           |Почта: $email
           |Группа: $group
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

    fun signUp(course: Course, client: Client): String {
        val timeNow: Instant = Calendar.getInstance().toInstant()

        if (course.expiryDate.compareTo(timeNow) > 1) {
            // todo: какая то реакция должна быть я хз какая
            // println("Время истекло.")
            return "Время истекло."
        }
        if (course.clientId == client.id) {
            //todo: какая то реакция должна быть я хз какая
            //println("Вы уже зарегестрированы.")
            return "Вы уже зарегестрированы."
        }
        submitApplicationUseCase.invoke(client.id,course.id)

        return """Вы записаны."""
    }
}
