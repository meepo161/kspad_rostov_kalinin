package ru.avem.stand.modules.r.tests.pi.maxm

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class MaxMView(title: String = "МАКСИМУМ (МОМЕНТ)", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MaxM

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MaxM()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox {
            hbox {
                minWidth = 820.0
                prefWidth = 820.0
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 400.0
                    prefWidth = 400.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("U AB, В", MaxMData::UAB.getter)
                    column("U BC, В", MaxMData::UBC.getter)
                    column("U CA, В", MaxMData::UCA.getter)
                    column("I A, А", MaxMData::IA.getter)
                    column("I B, А", MaxMData::IB.getter)
                    column("I C, А", MaxMData::IC.getter)
                }
            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 150.0
                    prefWidth = 150.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("P1, кВт", MaxMData::P1.getter)
                    column("cos φ", MaxMData::cos.getter)
                }
            }
        }
        hbox(spacing = 16) {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 220.0
                prefWidth = 220.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("n, об/мин", MaxMData::RPM.getter)
                column("M, Н⋅м", MaxMData::torque.getter)
            }
        }
        hbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 220.0
                prefWidth = 220.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.BOTTOM_RIGHT

                column("Результат", MaxMData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
