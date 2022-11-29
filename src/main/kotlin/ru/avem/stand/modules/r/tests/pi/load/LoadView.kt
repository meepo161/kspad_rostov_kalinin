package ru.avem.stand.modules.r.tests.pi.load

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class LoadView(title: String = "НАГР", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Load

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Load()
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

                    column("U AB, В", LoadData::UAB.getter)
                    column("U BC, В", LoadData::UBC.getter)
                    column("U CA, В", LoadData::UCA.getter)
                    column("I A, А", LoadData::IA.getter)
                    column("I B, А", LoadData::IB.getter)
                    column("I C, А", LoadData::IC.getter)
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

                    column("P1, кВт", LoadData::P1.getter)
                    column("f, Гц", LoadData::F.getter)
                    column("cos φ", LoadData::cos.getter)
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
                        column("Ось Y, мм/с", LoadData::v1y.getter)
                        column("Ось X, мм/с", LoadData::v1x.getter)
                        column("Ось Z, мм/с", LoadData::v1z.getter)
                    }
                    nestedColumn("Вибро (рабочая сторона) PG32") {
                        column("Ось Y, мм/с", LoadData::v2y.getter)
                        column("Ось X, мм/с", LoadData::v2x.getter)
                        column("Ось Z, мм/с", LoadData::v2z.getter)
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
                    minWidth = 350.0
                    prefWidth = 350.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("n, об/мин", LoadData::RPM.getter)
                    column("t BK1, °C", LoadData::tempAmb.getter)
                    column("t BK2, °C", LoadData::tempTI.getter)
                    column("t статора, °C", LoadData::T0After.getter)
                }
            }
        }
        hbox {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 270.0
                prefWidth = 270.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("КПД, о.е.", LoadData::efficiency.getter)
                column("s, %", LoadData::sk.getter)
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

                column("Время, с", LoadData::time.getter)
                column("Результат", LoadData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
