package ru.spbstu.eventbot.email

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.MultiPartEmail
import ru.spbstu.eventbot.domain.entities.Course
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.util.ByteArrayDataSource


fun sendCourseExpiredEmail(course: Course, byteArray: ByteArray) {
    val email: MultiPartEmail = MultiPartEmail()
    email.hostName = "smtp.gmail.com"
    email.sslSmtpPort = "465"
    email.setAuthenticator(DefaultAuthenticator("login@gmail.com", "password"))
    //ПРЕДУПРЕЖДЕНИЕ!!! здесь должны быть реальные данные,
    //а также в почтовом ящике должны быть оключены несколько слоёв безопасности
    email.isSSLOnConnect = true

    email.setFrom("login@gmail.com")
    //здесь тоже
    email.setSubject(subject(course))
    email.setMsg(subject(course))
    email.addTo(course.client.email)

    val dataSource: DataSource = ByteArrayDataSource(byteArray, "text/csv")
    email.attach(dataSource, "список участников", "")
    email.send()
}