package ru.spbstu.eventbot.email

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.MultiPartEmail
import ru.spbstu.eventbot.domain.entities.Course
import javax.activation.DataSource
import javax.mail.util.ByteArrayDataSource

private const val XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

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
            addTo(course.client.email.value)

            val dataSource: DataSource = ByteArrayDataSource(applicantsTable, XLSX_MIME_TYPE)
            attach(
                dataSource,
                "${Strings.applicantsTableFilename(course)}.xlsx",
                Strings.ApplicantsTableDescription
            )
        }
        email.send()
    }
}
