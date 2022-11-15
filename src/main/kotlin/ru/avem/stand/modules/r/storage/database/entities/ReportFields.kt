package ru.avem.stand.modules.r.storage.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ReportFields : IntIdTable() {
    val protocol = reference("protocol", Reports)
    val key = varchar("key", 128)
    val value = varchar("value", 512)
}

class ReportField(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReportField>(ReportFields)

    var protocol by Report referencedOn ReportFields.protocol
    var key by ReportFields.key
    var value by ReportFields.value
}
