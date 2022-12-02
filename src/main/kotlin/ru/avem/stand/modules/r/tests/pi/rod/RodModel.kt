package ru.avem.stand.modules.r.tests.pi.rod

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import tornadofx.onChange
import kotlin.math.abs

object RodModel : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredData.time.value = "%.0f".format(abs(it * specifiedTestTime))
        }
    }

    val specifiedData = RodData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = RodData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = RodData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedPercentRod = 0.0
    var specifiedTestTime = 0.0

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var isNeedCollectCurrents = false

    @Volatile
    var currents = mutableListOf<Double>()

    @Volatile
    var measuredI = 0.0

    var measuredU: Double = 0.0
}
