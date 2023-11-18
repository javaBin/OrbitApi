package no.java.partner.mail

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization

interface SendMail  {
    fun sendMail(to:String,subject:String,content:String)

    companion object {
        var sendMailImpl:SendMail = DummySendMail()

        val SEND_FROM = "partner@java.no"
        val SEND_FROM_NAME = "JavaZone"
    }
}

class DummySendMail():SendMail {
    override fun sendMail(to: String, subject: String, content: String) {
        println("Sending '$subject' to '$to' -> $content")
    }

}

class SendGridSendMail(private val sendGridKey:String):SendMail {
    override fun sendMail(to: String, subject: String, content: String) {
        val sg = SendGrid(sendGridKey)
        val request = Request()

        val from = Email(SendMail.SEND_FROM, SendMail.SEND_FROM_NAME)
        val mailContent = Content("text/html", content)
        val mail = Mail()
        mail.setFrom(from)
        mail.setSubject(subject)

        val email = Email(to)

        val personalization = Personalization()
        personalization.addTo(email)

        mail.addPersonalization(personalization)
        mail.addContent(mailContent)
        request.method = Method.POST
        request.endpoint = "mail/send"
        request.body = mail.build()
        val response = sg.api(request)

    }

}