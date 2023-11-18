package no.java.partner.util

import java.io.File

private fun companyFromEmail(email: String):String {
    val atpos = email.indexOf("@")
    if (atpos == -1 || atpos+1 >= email.length) {
        return email
    }
    val left = email.substring(atpos+1)
    val end = left.indexOf(".")
    if (end == -1) {
        return left
    }
    return left.substring(0,end)
}

private data class FlatContactImport(
    val email:String,
    val name:String?,
    val phone:String?,
    val company:String?,
    val source:String
) {
    val companyRead:String = company?: companyFromEmail(email)

}

private fun String.nullIfEmpty():String? = this.ifEmpty { null }
object PartnerImporter {

    private fun importRow(row:String):FlatContactImport {
        val parts:List<String> = row.split(";")
        return FlatContactImport(
            email = parts[1],
            name = parts[0].nullIfEmpty(),
            phone = parts[2].nullIfEmpty(),
            company = parts[3].nullIfEmpty(),
            source = parts[4]
        )
    }

    fun readAllEmails(filename:String):List<String> {
        val allLines:List<String> = File(filename).readLines()
        val allRead:List<FlatContactImport> = allLines.map { importRow(it) }
        val companies:List<String> = allRead.map { it.companyRead.lowercase() }.toSet().toList().sorted()


        println("Read ${allLines.size}")
        println("Companies : ${companies.size}")
        //companies.forEach { println(it) }

        val emails:List<String> = allRead.map { it.email.lowercase() }.toSet().toList().sorted()

        /*
        emails.forEach {
            println(it)
        }*/
        println("Email size:" + emails.size)
        return emails
    }

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 1) {
            println("Usage <importfile>")
            return
        }
        readAllEmails(args[0])
    }
}