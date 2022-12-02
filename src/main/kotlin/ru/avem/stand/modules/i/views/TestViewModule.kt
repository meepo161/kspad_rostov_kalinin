package ru.avem.stand.modules.i.views

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.ImagePattern
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.common.prefill.isCancelAllTests
import ru.avem.stand.modules.r.common.prefill.isTestRunning
import ru.avem.stand.modules.r.tests.Protection
import ru.avem.stand.modules.r.tests.TestItemData
import tornadofx.*

abstract class TestViewModule(title: String, showOnStart: Boolean = false) : ViewModule(title, showOnStart) {
    abstract val test: Test

    var cancelAllTestsButton: Button by singleAssign()
    var stopReloadButton: Button by singleAssign()
    var nextTestButton: Button by singleAssign()

    var vBoxLog: VBox by singleAssign()

    override fun onBeforeShow() {
        super.onBeforeShow()
        currentWindow?.setOnCloseRequest {
            showCancelConfirmation(it, currentWindow, currentStage, test)
        }
    }

    override fun onDock() {
        super.onDock()
        test.start()
    }

    override val root = vbox(spacing = 16) {
        injectTest()
        padding = insets(16)

        val image = Image("translucent-background-kalinin.png")
        val backgroundFill = BackgroundFill(ImagePattern(image), CornerRadii.EMPTY, Insets.EMPTY)
        background = Background(backgroundFill)

        label(test.name) {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox(spacing = 16) {
            padding = insets(16)

            specifiedData()

            testData()

            protections()
        }.addClass(Styles.paneBoldBorders)

        hbox(spacing = 16) {
            alignment = Pos.CENTER

            cancelAllTestsButton = button("Отменить всё") {
                minWidth = 150.0
                graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_LEFT_BOLD).apply {
                    glyphSize = 65
                    fill = c("red")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    currentStage?.close()
                    isCancelAllTests = true
                    isTestRunning = false
                }
            }

            stopReloadButton = button("Стоп") {
                isDefaultButton = true
                minWidth = 150.0
                graphic = MaterialDesignIconView(MaterialDesignIcon.STOP).apply {
                    glyphSize = 65
                    fill = c("red")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    test.stopReload()
                }
            }

            nextTestButton = button("К следующему") {
                minWidth = 150.0
                graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_RIGHT_BOLD).apply {
                    glyphSize = 65
                    fill = c("black")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    currentStage?.close()
                    isTestRunning = false
                }
            }
        }

        anchorpane {
            scrollpane {
                anchorpaneConstraints {
                    leftAnchor = 0.0
                    rightAnchor = 0.0
                    topAnchor = 0.0
                    bottomAnchor = 0.0
                }
                minHeight = 90.0
                maxHeight = 90.0
                prefHeight = 90.0
                minWidth = 900.0
                prefWidth = 900.0
                vBoxLog = vbox {
                }.addClass(Styles.megaHard)

                vvalueProperty().bind(vBoxLog.heightProperty())
            }
        }
        hbox {
            progressbar(property = test.testModel.progressProperty) {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                useMaxWidth = true
            }
        }
    }.addClass(Styles.extraHard)

    abstract fun injectTest()

    abstract fun EventTarget.testData(): VBox

    fun EventTarget.specifiedData(): VBox {
        return vbox(2) {
            padding = insets(8)

            hboxConstraints {
                hGrow = Priority.NEVER
            }

            alignmentProperty().set(Pos.TOP_CENTER)

            label("Заданные значения") {
                alignment = Pos.CENTER
                minWidth = 240.0
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("P2, кВт", TestItemData::P.getter)
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("U, В", TestItemData::U.getter)
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("I, А", TestItemData::I.getter)
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("cos φ", TestItemData::cos.getter)
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("n, об/мин", TestItemData::RPM.getter)
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("f, Гц", TestItemData::F.getter)
            }
            tableview(observableList(test.testModel.testItemData)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("КПД, %", TestItemData::efficiency.getter)
            }
        }.addClass(Styles.paneBorders)
    }

    private fun EventTarget.protections(): VBox {
        return vbox(2) {
            padding = insets(8)

            hboxConstraints {
                hGrow = Priority.NEVER
            }

            alignmentProperty().set(Pos.TOP_CENTER)

            label("Состояние защит") {
                alignment = Pos.CENTER
                minWidth = 210.0
            }
            tableview(observableList(test.testModel.protections.overCurrentOI)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("Токовая ОИ", Protection::prop.getter).cellFormat {
                    text = it
                    this.tableRow.toggleClass(Styles.greenText, it == "В НОРМЕ")
                    this.tableRow.toggleClass(Styles.redText, it == "СРАБОТАЛА")
                }
            }
            tableview(observableList(test.testModel.protections.overCurrentVIU)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("Токовая ВИУ", Protection::prop.getter).cellFormat {
                    text = it
                    this.tableRow.toggleClass(Styles.greenText, it == "В НОРМЕ")
                    this.tableRow.toggleClass(Styles.redText, it == "СРАБОТАЛА")
                }
            }
            tableview(observableList(test.testModel.protections.doorsZone)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("Двери зоны", Protection::prop.getter).cellFormat {
                    text = it
                    this.tableRow.toggleClass(Styles.greenText, it == "В НОРМЕ")
                    this.tableRow.toggleClass(Styles.redText, it == "СРАБОТАЛА")
                }
            }
            tableview(observableList(test.testModel.protections.doorsSHSO)) {
                minHeight = 64.0
                maxHeight = 64.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("Двери ШСО", Protection::prop.getter).cellFormat {
                    text = it
                    this.tableRow.toggleClass(Styles.greenText, it == "В НОРМЕ")
                    this.tableRow.toggleClass(Styles.redText, it == "СРАБОТАЛА")
                }
            }
        }.addClass(Styles.paneBorders)
    }
}
