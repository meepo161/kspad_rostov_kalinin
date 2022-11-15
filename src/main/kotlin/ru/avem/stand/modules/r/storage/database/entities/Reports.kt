package ru.avem.stand.modules.r.storage.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Reports : IntIdTable() {
    val template = varchar("template", 64)

    val serialNumber = varchar("serialNumber", 128)
    val testType = varchar("testType", 256)
    val test = varchar("test", 512)

    val user1Name = varchar("user1Name", 128)
    val user2Name = varchar("user2Name", 128)

    val date = varchar("date", 10)
    val time = varchar("time", 8)
    val isSuccess = varchar("isSuccess", 5)
}

class Report(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Report>(Reports)

    var template by Reports.template

    var serialNumber by Reports.serialNumber
    var testType by Reports.testType
    var test by Reports.test

    var user1Name by Reports.user1Name
    var user2Name by Reports.user2Name

    var date by Reports.date
    var time by Reports.time

    var isSuccess by Reports.isSuccess

    val fields by ReportField referrersOn (ReportFields.protocol)

    var stringId: String = ""
        get() = id.toString()

    val filledFields
        get() = transaction {
            fields.toList()
        }

    var user2Correct: String = ""
        get() = if (user2Name != "admin") user2Name else user1Name

    override fun toString() = "$id. $serialNumber:$testType - $date $time [$test] Результат: $isSuccess"

    fun toMills(): Long {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        val localDate = LocalDateTime.parse("$date $time", formatter)
        return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}
