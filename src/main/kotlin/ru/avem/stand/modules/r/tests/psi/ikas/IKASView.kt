package ru.avem.stand.modules.r.tests.psi.ikas

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.r.tests.psi.mgr.MGRData
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class IKASView(title: String = "ИКАС", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: IKAS

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = IKAS()
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
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(
                    observableList(
                        test.testModel.measuredData,
                        test.testModel.calculatedR20Data
                    )
                ) {
                    minHeight = 97.0
                    maxHeight = 97.0
                    minWidth = 480.0
                    prefWidth = 480.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("", IKASData::descriptor.getter)
                    column("RAB | RA, Ом", IKASData::R1.getter)
                    column("RBC | RB, Ом", IKASData::R2.getter)
                    column("RCA | RC, Ом", IKASData::R3.getter)
                }
            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 160.0
                    prefWidth = 160.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("t BK1, °C", IKASData::tempAmb.getter)
                    column("t BK2, °C", IKASData::tempTI.getter)
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

                column("Результат", IKASData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
