package ru.avem.stand.modules.r.storage.protocol

import javafx.geometry.Pos
import javafx.stage.FileChooser
import ru.avem.stand.modules.r.storage.database.entities.Report
import ru.avem.stand.modules.r.storage.database.getAllProtocols
import tornadofx.*
import tornadofx.controlsfx.errorNotification
import tornadofx.controlsfx.infoNotification
import java.awt.Desktop

class ProtocolsController : Controller() {
    private val view: ProtocolsTab by inject()

    var sortedProtocols = observableList<Report>()

    fun fillProtocolsTable() {
        sortedProtocols.setAll(getAllProtocols().reversed())
    }

    fun openProtocol() {
        view.getSelectedProtocol()?.let {
            try {
                Desktop.getDesktop().open(saveProtocolAsWorkbook(it).toFile())
            } catch (e: Exception) {
                errorNotification(
                    title = "Не удалось сохранить протокол для открытия",
                    text = "Причина: $e",
                    position = Pos.BOTTOM_CENTER
                )
            }
        } ?: errorNotification(title = "Ошибка", text = "Протокол для открытия не выбран", position = Pos.BOTTOM_CENTER)
    }

    fun saveProtocolAs() {
        view.getSelectedProtocol()?.let {
            try {
                val chooseFiles =
                    chooseFile(
                        title = "Сохранить как...",
                        filters = arrayOf(FileChooser.ExtensionFilter("AVEM Protocol (*.xlsx)", "*.xlsx")),
                        mode = FileChooserMode.Save,
                        owner = view.currentWindow
                    )
                if (chooseFiles.isNotEmpty()) {
                    val path = saveProtocolAsWorkbook(it, chooseFiles.first()).toString()
                    infoNotification(
                        title = "Инфо",
                        text = "Протокол сохранён по пути $path",
                        position = Pos.BOTTOM_CENTER
                    )
                }
            } catch (e: Exception) {
                errorNotification(
                    title = "Не удалось сохранить протокол",
                    text = "Причина: $e",
                    position = Pos.BOTTOM_CENTER
                )
            }
        } ?: errorNotification(
            title = "Ошибка",
            text = "Протокол для сохранения не выбран",
            position = Pos.BOTTOM_CENTER
        )
    }
}
