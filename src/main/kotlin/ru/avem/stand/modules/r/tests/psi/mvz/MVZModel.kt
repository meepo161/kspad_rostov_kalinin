package ru.avem.stand.modules.r.tests.psi.mvz

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import tornadofx.*
import kotlin.math.abs

object MVZModel : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredDataDuring.time.value = "%.0f".format(abs(it * specifiedTestTime))
        }
    }

    var stage: Stage = Stage.BEFORE

    enum class Stage {
        BEFORE,
        DURING,
        AFTER
    }

    val measuredDataBefore = MVZData(descriptor = SimpleStringProperty("До"))
    val measuredDataDuring = MVZData(descriptor = SimpleStringProperty("U * 1.3"))
    val measuredDataAfter = MVZData(descriptor = SimpleStringProperty("После"))
    val storedData = MVZData(descriptor = SimpleStringProperty("Сохранённая"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedTestTime = 0.0

    var tolerance = 0.0

    var beforeFIP1U = 0.0
    var lastFIP1U = 0.0

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    @Volatile
    var measuredUAB = 0.0

    @Volatile
    var measuredUBC = 0.0

    @Volatile
    var measuredUCA = 0.0

    @Volatile
    var measuredU = 0.0

    @Volatile
    var diffIA = 0.0

    @Volatile
    var diffIB = 0.0

    @Volatile
    var diffIC = 0.0
}
