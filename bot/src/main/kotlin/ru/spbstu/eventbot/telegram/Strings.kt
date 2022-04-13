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
    const val RevokeApplication = "❌ Отозвать запись"

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
