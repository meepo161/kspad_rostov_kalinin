package ru.avem.stand.modules.r.storage

import javafx.collections.ObservableList
import ru.avem.stand.head
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.storage.database.entities.TestItem
import ru.avem.stand.modules.r.storage.database.getAllTestItems
import ru.avem.stand.modules.r.storage.users.UsersRepository
import tornadofx.*

object Properties {
    val standData = StandData()

    val users = UsersRepository.users

    val testItems: ObservableList<TestItem>
        get() = getAllTestItems().observable()

    lateinit var tests: List<Test>

    fun initTestsData() {
        tests = head.tests
    }
}
