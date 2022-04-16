package ru.spbstu.eventbot.email

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.MultiPartEmail
import ru.spbstu.eventbot.domain.entities.Course
import javax.activation.DataSource
import javax.mail.util.ByteArrayDataSource

class EmailSender(private val emailSecrets: EmailSecrets) {
    fun sendCourseExpired(course: Course, applicantsTable: ByteArray) {
        val email = MultiPartEmail().apply {
            hostName = emailSecrets.hostname
            sslSmtpPort = emailSecrets.port
            isSSLOnConnect = true
            setAuthenticator(DefaultAuthenticator(emailSecrets.username, emailSecrets.password))
            setFrom(emailSecrets.from)

            subject = Strings.courseExpiredSubject(course)
            setMsg(Strings.courseExpiredMessage(course))
            addTo(course.client.email)

            val dataSource: DataSource = ByteArrayDataSource(applicantsTable, "text/csv")
            attach(
                dataSource,
                "${Strings.applicantsTableFilename(course)}.csv",
                Strings.ApplicantsTableDescription
            )
        }
        email.send()
    }
}
