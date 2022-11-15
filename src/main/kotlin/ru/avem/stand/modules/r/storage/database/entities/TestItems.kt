package ru.avem.stand.modules.r.storage.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object TestItems : IntIdTable() {
    val name = varchar("name", 256)
    val level = integer("level")
}

class TestItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItem>(TestItems)

    var name by TestItems.name
    var level by TestItems.level

    val fieldsIterable by TestItemField referrersOn (TestItemFields.testItem)

    val fields: Map<String, TestItemField>
        get() {
            return transaction { fieldsIterable.map { it.key to it }.toMap() }
        }

    override fun toString() = name
}
