package no.java.partner.util

import no.java.partner.mail.DummySendMail
import no.java.partner.mail.SendGridSendMail
import no.java.partner.mail.SendMail
import java.io.File
import java.lang.Exception

object PartnerMailSender {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 2) {
            println("Usage mailcontentfile maillist")
            return
        }
        val sendgridkey:String? = System.getenv("SENDGRID_KEY")

        val sendMail:SendMail = sendgridkey?.let { SendGridSendMail(it) }?:DummySendMail()

        val content = File(args[0]).readText()
        //val mailList:List<String> = PartnerImporter.readAllEmails(args[1])

        val mailList = listOf("per.bergsjo.andresen@accenture.com","n.thesen.laeskogen@accenture.com")

        println("Send got ${mailList.size}")



        for (toemail in mailList) {
            try {
                sendMail.sendMail(toemail, "JavaZone 2024 Partnermeeting #1", content)
                println ("Sent to $toemail")
            } catch (e:Exception) {
                println ("***Error sending to $toemail")
            }

        }

        println("Done")

    }
}