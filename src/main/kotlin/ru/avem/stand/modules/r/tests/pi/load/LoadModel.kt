package ru.avem.stand.modules.r.tests.pi.load

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import tornadofx.onChange
import kotlin.math.abs

object LoadModel : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredData.time.value = "%.0f".format(abs(it * specifiedTestTime))
        }
    }

    val specifiedData = LoadData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = LoadData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = LoadData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var syncRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedTestTime = 0.0

    var RCold = 0.0

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
    var measuredP2 = 0.0

    @Volatile
    var isNeedAbsTorque = false

    @Volatile
    var isStaticErrorTorqueSet = false
    @Volatile
    var staticErrorTorque = 0.0
    @Volatile
    var measuredTorque = 0.0

    @Volatile
    var fLM = 0.0

    @Volatile
    var isTIDirectionRight = true

    @Volatile
    var isContinueAgree = false

    var tempTI = 0.0
    var maxTemp = 0.0

    @Volatile
    var status = 0
    @Volatile
    var measuredR = 0.0
}
