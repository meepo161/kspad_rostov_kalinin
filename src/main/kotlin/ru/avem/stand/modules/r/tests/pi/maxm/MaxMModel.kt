package ru.avem.stand.modules.r.tests.pi.maxm

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import ru.avem.stand.modules.r.tests.AmperageStage
import ru.avem.stand.utils.ThinningBlob

object MaxMModel : TestModel() {
    val specifiedData = MaxMData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = MaxMData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = MaxMData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var syncRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    @Volatile
    var measuredU: Double = 0.0

    @Volatile
    var measuredP1 = 0.0

    @Volatile
    var isNeedAbsTorque = false

    @Volatile
    var isStaticErrorTorqueSet = false
    @Volatile
    var staticErrorTorque = 0.0
    @Volatile
    var measuredTorque = 0.0

    @Volatile
    var measuredTorqueMax = 0.0

    @Volatile
    var fLM = 0.0

    @Volatile
    var isLMDirectionRight = true

    @Volatile
    var isTIDirectionRight = true

    @Volatile
    var isContinueAgree = false

    val thinningMap = mapOf(measuredData.torque to ThinningBlob())
}
