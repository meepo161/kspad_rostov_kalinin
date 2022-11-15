package ru.avem.stand.modules.r.tests.psi.idle

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class IdleView(title: String = "ХХ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Idle

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Idle()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
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

                    column("U AB, В", IdleData::UAB.getter)
                    column("U BC, В", IdleData::UBC.getter)
                    column("U CA, В", IdleData::UCA.getter)
                    column("I A, А", IdleData::IA.getter)
                    column("I B, А", IdleData::IB.getter)
                    column("I C, А", IdleData::IC.getter)
                }
            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 200.0
                    prefWidth = 200.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("P1, кВт", IdleData::P1.getter)
                    column("f, Гц", IdleData::F.getter)
                    column("cos φ", IdleData::cos.getter)
                }
            }
        }
        hbox {
            hbox {
                minWidth = 820.0
                prefWidth = 820.0
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 91.0
                    maxHeight = 91.0
                    minWidth = 590.0
                    prefWidth = 590.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    nestedColumn("Вибро (полевая сторона) PG31") {
                        column("Ось Y, мм/с", IdleData::v1y.getter)
                        column("Ось X, мм/с", IdleData::v1x.getter)
                        column("Ось Z, мм/с", IdleData::v1z.getter)
                    }
                    nestedColumn("Вибро (рабочая сторона) PG32") {
                        column("Ось Y, мм/с", IdleData::v2y.getter)
                        column("Ось X, мм/с", IdleData::v2x.getter)
                        column("Ось Z, мм/с", IdleData::v2z.getter)
                    }
                }
            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 220.0
                    prefWidth = 220.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("n об/мин", IdleData::RPM.getter)
                    column("t BK1, °C", IdleData::tempAmb.getter)
                    column("t BK2, °C", IdleData::tempTI.getter)
                }
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

                column("Время, с", IdleData::time.getter)
                column("Результат", IdleData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
