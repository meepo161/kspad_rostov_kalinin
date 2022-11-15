package ru.avem.stand.modules.r.storage.protocol

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.text.Text
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.entities.Report
import ru.avem.stand.modules.r.storage.database.getAllProtocols
import tornadofx.*
import tornadofx.controlsfx.warningNotification
import java.awt.Desktop
import java.text.SimpleDateFormat

class ProtocolsTab : View("Протоколы") {
    private val controller: ProtocolsController by inject()

    var filterField: TextField by singleAssign()
    var tableProtocols: TableView<Report> by singleAssign()
    var comboBoxSN: ComboBox<String> by singleAssign()

    private val validationCtx = ValidationContext()

    private lateinit var serialNumbersToProtocols: Map<String, List<Report>>

    private val tests = anchorpane {
        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 16.0
                topAnchor = 16.0
            }
            alignment = Pos.CENTER

            hbox(spacing = 16.0) {
                alignment = Pos.CENTER

                filterField = textfield {
                    promptText = "Фильтр"
                    textProperty().onChange {
                        tableProtocols.items = controller.sortedProtocols.filter {
                            it.stringId.toLowerCase().contains(textProperty().value.toLowerCase()) ||
                                    it.serialNumber.toLowerCase().contains(textProperty().value.toLowerCase()) ||
                                    it.test.toLowerCase().contains(textProperty().value.toLowerCase()) ||
                                    it.date.toLowerCase().contains(textProperty().value.toLowerCase()) ||
                                    it.time.toLowerCase().contains(textProperty().value.toLowerCase()) ||
                                    it.user1Name.toLowerCase().contains(textProperty().value.toLowerCase()) ||
                                    it.user2Name.toLowerCase().contains(textProperty().value.toLowerCase())
                        }.observable()
                    }
                }
            }

            tableProtocols = tableview(controller.sortedProtocols) {
                minHeight = 500.0
                minWidth = 1020.0
                vboxConstraints {
                    vGrow = Priority.ALWAYS
                }

                columnResizePolicy = SmartResize.POLICY
                placeholder = Text("Список пуст")

                onDoubleClick {
                    controller.openProtocol()
                }

                column("№ в БД", Report::stringId) {
                    minWidth = 100.0

                    setComparator { o1, o2 -> o1.toLong().compareTo(o2.toLong()) }
                }
                column("Зав. №", Report::serialNumber) {
                    minWidth = 100.0
                }
                column("Испытание", Report::test) {
                    minWidth = 800.0
                }.remainingWidth()
                column("Дата", Report::date) {
                    minWidth = 100.0

                    val datePattern = SimpleDateFormat("dd.MM.yyyy")
                    setComparator { o1, o2 -> datePattern.parse(o1).compareTo(datePattern.parse(o2)) }
                }.remainingWidth()
                column("Время", Report::time) {
                    minWidth = 100.0
                }.remainingWidth()
            }
            hbox(spacing = 16.0) {
                alignment = Pos.CENTER

                button("Открыть") {
                    onAction = EventHandler {
                        controller.openProtocol()
                    }
                }

                button("Сохранить как...") {
                    onAction = EventHandler {
                        controller.saveProtocolAs()
                    }
                }
            }
        }
    }

    private val serialNumbers = anchorpane {
        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 16.0
                topAnchor = 16.0
            }
            alignment = Pos.CENTER

            hbox(spacing = 16.0) {
                alignment = Pos.CENTER

                label("Заводской номер")

                comboBoxSN = combobox {
                    prefWidth = 200.0

                    validationCtx.addValidator(this, selectionModel.selectedItemProperty()) { selected ->
                        val filteredItems = items.filter { it == selected }
                        if (filteredItems.isNullOrEmpty()) error("Невалидный заводской номер") else null
                    }

                    setOnShowing {
                        serialNumbersToProtocols = getAllProtocols().groupBy(Report::serialNumber)

                        items = serialNumbersToProtocols.keys.toList().filter { it.isNotBlank() }.observable()
                    }
                }
            }

            button("Открыть") {
                action {
                    try {
                        serialNumbersToProtocols[comboBoxSN.selectionModel.selectedItem]?.let { protocols ->
                            val testTitlesToProtocols = protocols.groupBy(Report::test)

                            val testToProtocol = mutableMapOf<String, Report>()

                            testTitlesToProtocols.forEach { (testTitle, protocols) ->
                                testToProtocol[testTitle] = protocols.maxByOrNull(Report::id)!!
                            }
                            Desktop.getDesktop()
                                .open(
                                    saveProtocolsAsWorkbook(
                                        testToProtocol.values.toList(),
                                        "common.xlsx"
                                    ).toFile()
                                )
                            comboBoxSN.selectionModel.select(-1)
                            return@action

                        } ?: warningNotification(
                            "Невалидный заводской номер",
                            "Невалидный заводской номер",
                            Pos.CENTER,
                            owner = this@ProtocolsTab.currentWindow
                        )
                    } catch (e: Exception) {
                        warningNotification(
                            "Внимание",
                            "Не указан файл общего шаблона протокола",
                            Pos.CENTER,
                            owner = this@ProtocolsTab.currentWindow
                        )
                    }
                }
            }.removeWhen(validationCtx.valid.not())
        }
    }

    override val root = tabpane {
        tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.UNAVAILABLE)
        prefWidth = Properties.standData.width
        prefHeight = Properties.standData.height

        tab("По заводскому номеру") {
            content = serialNumbers
        }
        tab("По испытаниям") {
            content = tests
        }
    }.addClass(Styles.regularLabels)

    override fun onDock() {
        super.onDock()
        validationCtx.validate()
    }

    fun getSelectedProtocol() =
        if (tableProtocols.selectionModel.selectedIndex >= 0) tableProtocols.selectionModel.selectedItem else null
}
