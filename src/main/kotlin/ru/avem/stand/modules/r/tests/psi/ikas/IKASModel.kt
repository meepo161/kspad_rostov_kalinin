package ru.avem.stand.modules.r.tests.psi.ikas

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel

object IKASModel : TestModel() {
    val specifiedData = IKASData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = IKASData(descriptor = SimpleStringProperty("Измеренные"))
    val calculatedData = IKASData(descriptor = SimpleStringProperty("Рассчитанные фазные"))
    val calculatedR20Data = IKASData(descriptor = SimpleStringProperty("Приведённые к 20℃"))
    val percentData = IKASData(descriptor = SimpleStringProperty("% отношение к среднему"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedR = 0.0
    var specifiedRtK = 0.0

    @Volatile
    var status = 0

    @Volatile
    var measuredR = 0.0
}
