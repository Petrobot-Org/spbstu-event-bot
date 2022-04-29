package ru.spbstu.eventbot.telegram

import ru.spbstu.eventbot.domain.entities.*
import ru.spbstu.eventbot.domain.usecases.SubmitApplicationUseCase
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Strings {
    const val InvalidName = "Вы ввели некорректное ФИО. Попробуйте снова."
    const val InvalidEmail = "Вы ввели некорректную почту. Попробуйте снова."
    const val InvalidGroup = "Вы ввели некорректный номер группы. Убедитесь, что вы вводите группу ВШПИ и попробуйте снова."
    const val RequestName = "Давайте познакомимся.\nВвведите своё ФИО "
    const val RequestEmail = "Теперь нужен адрес электронной почты."
    const val RequestGroup = "Укажите номер своей группы"
    const val RegistrationRetry = "Тогда начинаем заново"
    const val RequestYesNo = "Напишите да или нет"
    const val RegisteredSuccessfully = "Вы *успешно* зарегистрированы.\nТеперь вы можете записываться на курсы."

    const val RequestClientName = "Введите название организации-заказчика"
    const val RequestClientEmail = "Теперь введите адрес электронной почты заказчика"
    const val RequestClientUserId = "Введите Id телеграма заказчика (напишите \"нет\", если нет)"
    const val ClientRegisteredSuccessfully = "Новый заказчик был успешно создан"
    const val InvalidClientName = "Вы ввели некорректное ФИО заказчика. Попробуйте снова."
    const val InvalidClientUserId = "Id заказчика должно быть целым числом. Попробуйте снова."

    const val SelectClient = "От кого?"
    const val RequestTitle = "Введите название курса"
    const val RequestDescription = "Введите краткую информацию о содержании курса"
    const val RequestAdditionalQuestion = "Если помимо основной информации (номер группы, ФИО, адрес электронной " +
            "почты) требуется *дополнительная информация*, укажите её в форме вопроса.\nЕсли нет, то напишите \"нет\"."
    const val RequestExpiryDate = "Введите дату дэдлайна подачи заявок на курс в формате *дд.ММ.гггг чч:мм*"
    const val InvalidDate = "Неправильный формат даты\nТребуемый формат *дд.ММ.гггг чч:мм*"
    const val ErrorRetry = "Что-то пошло не так. Начинаем заново."
    const val CreatedNewCourseSuccessfully = "Вы успешно создали курс.\nТеперь студенты получат оповещение о появлении вашего курса и смогут подать на него заявку."
    const val ConfirmGroupMatcher = "✅ Подтвердить"
    const val ExpiredGroupMatcher = "Истёк"

    const val UnknownCommand = "Введена неизвестная команда."
    const val UnauthorizedError = "У вас недостаточно прав для этой команды.\nСвяжитесь с оператором бота, в случае ошибки."
    const val AvailableCoursesHeader = "Доступные курсы"
    const val NoSuchCourse = "Этого курса не существует."
    const val NoSuchClient = "Этого заказчика не существует."
    const val NoApplicants = "Никто ещё не подал заявку на этот курс."
    const val SubmitApplication = "✅ Записаться"
    const val RevokeApplication = "❌ Отозвать запись"
    const val SubmitError = "Не удалось подать заявку."
    const val RevokeError = "Не удалось отозвать заявку."

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
        
         Для этого тебе нужно зарегистрироваться (нажми на кнопку «Регистрация» или введи команду /register).
         
         Здесь хранится список открытых для записи курсов, а также мы будем оповещать тебя о новых курсах, как только они появятся.
    """.trimIndent()

    const val AlreadyApplied = "Заявка уже отправлена"
    const val CourseExpired = "Время подачи заявок для записи на этот курс истекло"
    const val NotRegistered = "Вы не зарегистрированы.\nДля подачи заявки необходимо зарегистрироваться. Для этого нажми на кнопку «Регистрация» или введи команду /register"
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
        """*Внимание!*
            |Открыта запись на новый курс: ${course.title}
            |Курс предоставляется ${course.client.name}
            |Описание курса: ${course.description}
            |Дэдлайн подачи заявки: ${dateTimeFormatter.format(course.expiryDate)} 
            |
            |Если вы получили это оповещение, то вы можете подать *заявку* на этот курс. Ваших навыков и компетенций достаточно по мнению автора курса.
            |
            |Для *подачи* заявки необходимо нажать кнопку «Курсы», выбрать этот курс по названию и нажать кнопку «Записаться»
            |
            |В случае, если вы передумаете участвовать в этом курсе, вы можете *отозвать* заявку.
            |Для этого необходимо нажать кнопку «Отозвать запись»
            """.trimMargin()

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

    fun requestAdditionalInfo(additionalQuestion: String)=
        """
            |Для подачи заявки на запись на этот курс необходимо ввести дополнительную информацию
            |
            | $additionalQuestion
            | 
            |Укажите эту информацию в ответном сообщении.          
        """.trimMargin()

    fun groupMatcher(regex: Regex) =
        """|Фильтр групп
           |Получившийся regex: $regex
        """.trimMargin()

    fun selectedButton(text: String) = "☑️️ $text"

    fun studyYear(year: Year) = "${year.value} курс"

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
