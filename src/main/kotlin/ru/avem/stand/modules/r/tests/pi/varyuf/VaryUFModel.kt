package ru.avem.stand.modules.r.tests.pi.varyuf

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import tornadofx.onChange
import kotlin.math.abs

object VaryUFModel : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredData1.time.value = "%.0f".format(abs(it * specifiedTestTime))
        }
    }

    val specifiedData = VaryUFData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData1 = VaryUFData(descriptor = SimpleStringProperty("0.8*Uн | 0.94*fН"))
    val measuredData2 = VaryUFData(descriptor = SimpleStringProperty("1.1*Uн | 1.03*fН"))
    val storedData1 = VaryUFData(descriptor = SimpleStringProperty("Сохранённые"))
    val storedData2 = VaryUFData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedTestTime = 15.0

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    var measuredU: Double = 0.0

    @Volatile
    var isSecondMode = false
}
