package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.*
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
    const val RequestYesNo = "Напишите да или нет"
    const val RegisteredSuccessfully = "Успешная регистрация"

    const val RequestClientName = "Имя заказчика"
    const val RequestClientEmail = "Адрес электронной почты заказчика"
    const val RequestClientUserId = "Id телеграма заказчика (напишите \"нет\", если нет)"
    const val ClientRegisteredSuccessfully = "Клиент создан"
    const val InvalidClientName = "Некорректное имя заказчика. Попробуйте снова."
    const val InvalidClientUserId = "Id заказчика должно быть целым числом. Попробуйте снова."

    const val SelectClient = "От кого?"
    const val RequestTitle = "Название курса"
    const val RequestDescription = "Описание курса"
    const val RequestAdditionalQuestion = "Если помимо основной информации (номер группы, ФИО, адрес электронной " +
        "почты) требуется дополнительная информация, укажите её в форме вопроса. Если нет, то напишите \"нет\"."
    const val RequestExpiryDate = "Дэдлайн подачи заявок на курс (дд.ММ.гггг чч:мм)"
    const val RequestGroupMatcher = "Regex группы" // TODO: ...
    const val InvalidDate = "Неправильный формат даты"
    const val ErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val CreatedNewCourseSuccessfully = "Курс был успешно создан"

    const val UnknownCommand = "Неизвестная команда"
    const val UnauthorizedError = "Недостаточно прав"
    const val AvailableCoursesHeader = "Доступные курсы"
    const val NoSuchCourse = "Этого курса не существует"
    const val NoSuchClient = "Этого клиента не существует"
    const val NoApplicants = "Никто ещё не подал заявку на этот курс"
    const val SubmitApplication = "✅ Записаться"
    const val RevokeApplication = "❌ Отозвать запись"
    const val SubmitError = "Не получилось подать заявку"
    const val RevokeError = "Не получилось отозвать заявку"

    const val HelpCommands = "Список доступных команд: \n"
    const val RegisterDescription = " /register - регистрация студентов в боте\n"
    const val StartDescription = "/start -  приветственное слово с кратким описанием функционала\n"
    const val CoursesDescription = "/courses - получение списка актуальных курсов\n"
    const val NewClientDescription = "/newclient - создание нового заказчика(партнёра, предоставляющего курса)\n"
    const val GetApplicantsDescription = "/getapplicants - получение списка студентов, подавших заявку на курс\n"
    const val NewCourseDescription = "/newcourse - создание нового курса\n"

    val HelpStart = """
        *Приветствую!*
         С помощью этого бота ты можешь подать заявку на участие в дополнительных курсах партнёров ВШПИ, не утруждая себя вводом одних и тех же личных данных каждый раз. 
         Для этого тебе нужно зарегистрироваться (нажми на кнопку «Регистрация» или введи команду /register.
         Здесь хранится список открытых для записи курсов, а также мы будем оповещать тебя о новых курсах, как только они появятся.
    """.trimIndent()

    const val AlreadyApplied = "Заявка уже отправлена"
    const val CourseExpired = "Время на приём заявок истекло"
    const val NotRegistered = "Вы не зарегистрированы"
    const val CourseNotFound = "Такого курса нет"

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
        """Сбор заявок на курс "${course.title}" завершён"""

    fun notificationAboutNewCourse(course: Course) =
        """новый курс: ${course.title}"""

    fun registrationConfirmation(name: FullName, email: Email, group: Group) =
        """|Имя: $name
           |Почта: $email
           |Группа: $group
           |Верно?
        """.trimMargin()

    fun clientRegistrationConfirmation(name: ClientName, email: Email, userId: Long?) =
        """|Имя: $name
           |Почта: $email
           |id: ${userId ?: "нет"}
           |Верно?
        """.trimMargin()

    fun newCourseCreationConfirmation(title: CourseTitle, description: CourseDescription, additionalQuestion: String?, expiryDate: Instant, groupMatcher: Regex) =
        """|Название курса: $title
           |Описание курса: $description
           |Дополнительный вопрос: ${additionalQuestion ?: "нет"}
           |Дэдлайн подачи заявки: ${dateTimeFormatter.format(expiryDate)}
           |Regex групп: $groupMatcher
           |Верно?
        """.trimMargin()

    fun courseDetails(course: Course) =
        """|*${course.title}* от _${course.client.name}_
           |
           |🕒 До ${dateTimeFormatter.format(course.expiryDate)}
           |${course.description}
        """.trimMargin()

    fun <T> csvOf(
        headers: List<String>,
        data: List<T>,
        itemBuilder: (T) -> List<String>
    ) = buildString {
        append(headers.joinToString(",") { "\"$it\"" })
        append("\n")
        data.forEach { item ->
            append(itemBuilder(item).joinToString(",") { "\"$it\"" })
            append("\n")
        }
    }

    fun applicantsInfo(applications: List<Application>): String {
        val csv = csvOf(
            listOf("ФИО студента", "Группа", "Почта", "Доп. информация"),
            applications
        ) {
            listOf(it.student.fullName.toString(), it.student.group.toString(), it.student.email.toString(), it.additionalInfo.toString())
        }
        return csv
    }
}
