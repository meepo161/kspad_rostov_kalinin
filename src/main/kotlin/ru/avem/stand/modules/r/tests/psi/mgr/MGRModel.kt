package ru.avem.stand.modules.r.tests.psi.mgr

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.avem.stand.modules.i.TestModel
import tornadofx.onChange
import kotlin.math.abs

object MGRModel : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredData.time.value = "%.0f".format(abs(it * specifiedTestTime))
        }
    }

    val specifiedData = MGRData()
    val measuredData = MGRData()

    var specifiedU = 0.0
    var specifiedI = 0.0
    val specifiedTestTime = 60

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedUMGR = 0.0
    var specifiedRMGR = 0.0
}
