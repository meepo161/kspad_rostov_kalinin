package ru.avem.stand.modules.r.tests.psi.kz

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import ru.avem.stand.modules.r.tests.AmperageStage

object KZModel : TestModel() {
    val specifiedData = KZData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = KZData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = KZData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedPKZ = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedUKZ = 0.0
    var specifiedIKZ = 0.0

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    var measuredU: Double = 0.0
}
