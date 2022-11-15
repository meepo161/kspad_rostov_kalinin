package ru.avem.stand.modules.r.tests.pi.load

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class LoadData(
    val descriptor: StringProperty,

    val R1: StringProperty = SimpleStringProperty(""),
    val R2: StringProperty = SimpleStringProperty(""),
    val R3: StringProperty = SimpleStringProperty(""),

    val U: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty(""),
    val P1: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),
    val P2: StringProperty = SimpleStringProperty(""),
    val efficiency: StringProperty = SimpleStringProperty(""),
    val sk: StringProperty = SimpleStringProperty(""),

    val v1: StringProperty = SimpleStringProperty("Вибро (полевая сторона) PG31"),
    val v1x: StringProperty = SimpleStringProperty(""),
    val v1y: StringProperty = SimpleStringProperty(""),
    val v1z: StringProperty = SimpleStringProperty(""),

    val v2: StringProperty = SimpleStringProperty("Вибро (рабочая сторона) PG32"),
    val v2x: StringProperty = SimpleStringProperty(""),
    val v2y: StringProperty = SimpleStringProperty(""),
    val v2z: StringProperty = SimpleStringProperty(""),

    val torque: StringProperty = SimpleStringProperty(""),
    val RPM: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val R0After: StringProperty = SimpleStringProperty(""),
    val R1After: StringProperty = SimpleStringProperty(""),
    val R1TimeAfter: StringProperty = SimpleStringProperty(""),
    val R2After: StringProperty = SimpleStringProperty(""),
    val R2TimeAfter: StringProperty = SimpleStringProperty(""),
    val R3After: StringProperty = SimpleStringProperty(""),
    val R3TimeAfter: StringProperty = SimpleStringProperty(""),
    val T0After: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
)
