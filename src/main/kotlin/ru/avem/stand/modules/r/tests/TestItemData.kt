package ru.avem.stand.modules.r.tests

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class TestItemData(
    val P: StringProperty = SimpleStringProperty(""),
    val U: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty(""),
    val RPM: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),
    val efficiency: StringProperty = SimpleStringProperty(""),
)
