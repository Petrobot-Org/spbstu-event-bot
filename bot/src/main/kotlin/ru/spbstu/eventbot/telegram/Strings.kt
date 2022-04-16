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

    const val RequestClientName = "Имя заказчика"
    const val RequestClientEmail = "Адрес электронной почты заказчика"
    const val RequestClientUserId = "Id телеграма заказчика (напишите \"нет\", если нет)"
    const val ClientRegistrationRetry = "Тогда начинаем заново"
    const val ClientRegistrationErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val ClientRegisteredSuccessfully = "Клиент создан"
    const val InvalidClientName = "Некорректное имя заказчика. Попробуйте снова."
    const val InvalidClientUserId = "Id заказчика должно быть целым числом. Попробуйте снова."

    const val SelectClient = "От кого?"
    const val RequestTitle = "Название курса"
    const val RequestDescription = "Описание курса"
    const val RequestAdditionalQuestion = "Если помимо основной информации (номер группы, ФИО, адрес электронной " +
        "почты) требуется дополнительная информация, укажите её в форме вопроса. Если нет, то напишите \"нет\"."
    const val RequestExpiryDate = "Дэдлайн подачи заявок на курс (дд.ММ.гггг чч:мм)"
    const val InvalidDate = "Неправильный формат даты"
    const val CreationErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val CreatedNewCourseSuccessfully = "Курс был успешно создан"

    const val UnknownCommand = "Неизвестная команда"
    const val UnauthorizedError = "Недостаточно прав"
    const val AvailableCoursesHeader = "Доступные курсы"
    const val NoSuchCourse = "Этого курса не существует"
    const val NoSuchClient = "Этого клиента не существует"
    const val NoApplicants = "Никто ещё не подал заявку на этот курс"
    const val SubmitApplication = "✅ Записаться"

    const val HelpCommands = "I help you!"
    const val HelpStart = "I help you for start work with me!"

    const val ButtonCourses = "Курсы"
    const val ButtonRegister = "Регистрация"
    const val ButtonNewClient = "Новый заказчик"
    const val ButtonNewCourse = "Новый курс"

    val PositiveAnswers = setOf("да", "ага", "угу", "д", "yes", "ye", "yeah", "y")
    val NegativeAnswers = setOf("no", "net", "n", "нет", "не", "н")

    private val dateTimeFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG)
        .withZone(ZoneId.systemDefault())

    fun courseExpiredNotification(course: Course) =
        """время записи на курс "${course.title}" истекло""".trimMargin()


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

    fun newCourseCreationConfirmation(title: String, description: String, additionalQuestion: String?, expiryDate: Instant) =
        """|Название курса: $title
           |Описание курса: $description
           |Дополнительный вопрос: $additionalQuestion
           |Дэдлайн подачи заявки: ${dateTimeFormatter.format(expiryDate)}
           |Верно?
        """.trimMargin()

    fun courseDetails(course: Course) =
        """|*${course.title}*
           |
           |🕒 До ${dateTimeFormatter.format(course.expiryDate)}
           |${course.description}
        """.trimMargin()

    // TODO: Убрать (заменить на генерацию CSV файла)
    fun applicantsInfo(applicants: List<Student>): String {
        var listOfApplicants = ""
        for (applicant in applicants) {
            listOfApplicants += """|ФИО студента: ${applicant.fullName}
           |Группа: ${applicant.group}
           |Почта: ${applicant.email}
           |-------------------------- 
            """.trimMargin() // Pochernin-style разделитель строк --------------------------
        }
        return listOfApplicants
    }
}
