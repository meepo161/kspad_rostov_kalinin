package ru.avem.stand.modules.r.common.prefill

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.storage.database.entities.TestItem

object PreFillModel {
    val serialNumberProp: StringProperty = SimpleStringProperty("")
    var testTypeProp: Property<TestItem> = SimpleObjectProperty()
    var selectedTests: MutableList<Test> = mutableListOf()
}
