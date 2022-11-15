package ru.avem.stand.modules.r.tests.psi.idle

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class IdleData(
    val descriptor: StringProperty,

    val U: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),

    val P1: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty(""),

    val v1: StringProperty = SimpleStringProperty("Вибро (полевая сторона) PG31"),
    val v1x: StringProperty = SimpleStringProperty(""),
    val v1y: StringProperty = SimpleStringProperty(""),
    val v1z: StringProperty = SimpleStringProperty(""),

    val v2: StringProperty = SimpleStringProperty("Вибро (рабочая сторона) PG32"),
    val v2x: StringProperty = SimpleStringProperty(""),
    val v2y: StringProperty = SimpleStringProperty(""),
    val v2z: StringProperty = SimpleStringProperty(""),

    val RPM: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
)
