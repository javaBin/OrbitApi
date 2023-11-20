package no.java.partner.util

import org.jsonbuddy.JsonObject
import org.jsonbuddy.parse.JsonParser
import java.io.File
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

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

private data class CompanyImport(
    val id:String = UUID.randomUUID().toString(),
    val name:String,
    val domain:Set<String>,
    val contacs:List<FlatContactImport>
) {
    val reportValue:String = "'$name' ($domain) : ${contacs.size}"
}

private fun domainFromEmail(email: String):String {
    val pos = email.indexOf("@")
    if (pos == -1 || pos >= email.length-1) {
        return email
    }
    return email.substring(pos+1)
}

private fun String.nullIfEmpty():String? = this.ifEmpty { null }

private class MatchCompanyUtil(filname:String?) {
    private val aliasList:List<List<String>>
    init {
        aliasList = if (filname == null) emptyList() else File(filname).readLines().map { it.split(",") }
    }

    fun companyNameToUse(a:String,b:String):String? {
        if (a == b) {
            return a
        }
        val matching:List<String>? = aliasList.firstOrNull { it.contains(a) && it.contains(b) }
        if (matching != null) {
            return matching.first()
        }
        if ("$a as" == b) {
            return a
        }
        if ("$b as" == a) {
            return b
        }
        return null
    }
}

object PartnerImporter {
    private val SERVER_ADDRESS = "localhost:8080"
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



    private fun flatImport(filename: String):List<FlatContactImport> {
        val allLines:List<String> = File(filename).readLines()
        println("Read ${allLines.size}")
        val allRead:List<FlatContactImport> = allLines.map { importRow(it) }
        return allRead
    }

    fun mapForImport(filename: String,filenameDuplicates:String?) {
        val allRead = flatImport(filename)
        val companies:MutableList<CompanyImport> = mutableListOf()

        val matchCompanyUtil = MatchCompanyUtil(filenameDuplicates)

        for (readImport in allRead) {
            val companyName = readImport.companyRead.lowercase()
            var replacedNone = true
            companies.replaceAll {
                val matchedName:String? = matchCompanyUtil.companyNameToUse(it.name,companyName)
                if (matchedName != null) {
                    replacedNone = false
                    it.copy(
                        name = matchedName,
                        domain = it.domain + domainFromEmail(readImport.email),
                        contacs = it.contacs + readImport
                    )
                } else it
            }
            if (replacedNone) {
                companies.add(
                    CompanyImport(
                        name = companyName,
                        domain = setOf(domainFromEmail(readImport.email)),
                        contacs = listOf(readImport)
                    )
                )
            }
        }
        companies.sortBy { it.name }
        companies.forEach { println(it.reportValue) }
    }

    fun readAllEmails(filename:String):List<String> {
        val allRead = flatImport(filename)
        val companies:List<String> = allRead.map { it.companyRead.lowercase() }.toSet().toList().sorted()


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

    fun addPartner(name:String, domanins:List<String>):String {
        val url = URL(SERVER_ADDRESS + "/partner")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setDoOutput(true)
        PrintWriter(OutputStreamWriter(urlConnection.getOutputStream(), "utf-8")).use { printWriter ->
            payload.toJson(
                printWriter
            )
        }
        val restultObj:JsonObject = urlConnection.inputStream.use { JsonObject.read(it) }
        return restultObj.requiredString("")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 1) {
            println("Usage <importfile>")
            return
        }
        val filenameDuplicates:String? = if (args.size > 1) args[1] else null
        mapForImport(args[0],filenameDuplicates)
    }


}