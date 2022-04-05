package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.Course
import ru.spbstu.eventbot.domain.entities.Student
import java.time.Instant
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

    const val RequestClientName = "Имя клиента"
    const val RequestClientEmail = "Адрес электронной почты клиента"
    const val ClientRegistrationRetry = "Тогда начинаем заново"
    const val ClientRegistrationErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val ClientRegisteredSuccessfully = "Клиент создан"
    const val InvalidClientName = "Некорректное имя клиента. Попробуйте снова."

    const val RequestTitle = "Название курса"
    const val RequestDescription = "Описание курса"
    const val RequestAdditiinalQuestion = "Если помимо основной информации(номер группы, ФИО, номер почты) требуется дополнительная информация, укажите какая в форме вопроса"
    const val RequestExriryDate = "Дэдлайн подачи заявок на курс"
    const val CreationErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val CreatedNewCourseSuccessfully = "Курс был успешно создан"

    const val DontKnowWhatToDo = "Не знаю, что с этим делать"
    const val UnknownCommand = "Неизвестная команда"
    const val UnauthorizedError = "Недостаточно прав"
    const val AvailableCoursesHeader = "Доступные курсы"
    const val NoSuchCourse = "Этого курса не существует"
    const val NoApplicants = "Никто ещё не подал заявку на этот курс"
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

    fun clientRegistrationConfirmation(name: String, email: String) =
        """|Имя: $name
           |Почта: $email
           |Верно?
        """.trimMargin()

    fun newCourseCreationConfirmation(title: String, description: String, additionalQuestion: String?, expiryDate: Instant) =
        """|Название курса: $title
           |Описание курса: $description
           |Дополнительный вопрос: $additionalQuestion
           |Дэдлайн подачи заявки: $expiryDate
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

    fun applicantsInfo(applicants: List<Student>): String {
        var listOfApplicants = ""
        for (applicant in applicants) {
            listOfApplicants += """|ФИО студента: ${applicant.fullName}
           |Группа: ${applicant.group}
           |Почта: ${applicant.email}
           |--------------------------
        """.trimMargin()
        }
        return listOfApplicants
    }
}
