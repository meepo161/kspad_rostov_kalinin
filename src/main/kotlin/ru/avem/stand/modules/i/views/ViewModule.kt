package ru.avem.stand.modules.i.views

import javafx.scene.input.KeyCombination
import ru.avem.stand.modules.r.storage.Properties
import tornadofx.*

abstract class ViewModule(title: String, val showOnStart: Boolean = false) : View(title) {
    override fun onDock() {
        if (Properties.standData.isFullScreen) {
            modalStage!!.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        }

        modalStage!!.isFullScreen = Properties.standData.isFullScreen
        modalStage!!.isMaximized = Properties.standData.isMaximized
        modalStage!!.centerOnScreen()
        modalStage!!.isResizable = Properties.standData.isResizable
    }

    fun show() {
        TFXViewManager.openModal(this::class)
    }
}
