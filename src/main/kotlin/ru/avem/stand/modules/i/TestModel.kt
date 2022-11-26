package ru.avem.stand.modules.i

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.avem.stand.modules.r.tests.AmperageStage
import ru.avem.stand.modules.r.tests.ProtectionsData
import ru.avem.stand.modules.r.tests.TestItemData

abstract class TestModel {
    open val progressProperty: DoubleProperty = SimpleDoubleProperty()

    var amperageStage = AmperageStage.FROM_500_TO_5

    val testItemData = TestItemData()
    val protections = ProtectionsData()
}
