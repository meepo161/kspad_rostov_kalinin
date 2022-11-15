package ru.avem.stand.modules.r.common

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.layout.*
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.ViewModule
import ru.avem.stand.modules.i.views.showExitConfirmation
import ru.avem.stand.modules.r.common.authorization.AuthorizationModel
import ru.avem.stand.modules.r.common.editor.TestItemEditorTab
import ru.avem.stand.modules.r.common.prefill.PreFillTab
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.protocol.ProtocolsController
import ru.avem.stand.modules.r.storage.protocol.ProtocolsTab
import tornadofx.*

class AggregateView(title: String = Properties.standData.titleShort) :
    ViewModule(title, showOnStart = true) {
    private val protocolsController: ProtocolsController by inject()

    override fun onBeforeShow() {
        super.onBeforeShow()
        currentWindow?.setOnCloseRequest {
            if (Properties.standData.exitConfirmation) {
                showExitConfirmation(it, currentWindow)
            }
        }
    }

    override val root = tabpane {
        tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.UNAVAILABLE)
        prefWidth = Properties.standData.width
        prefHeight = Properties.standData.height

        background = Background(
            BackgroundImage(
                Image("background.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize(
                    BackgroundSize.AUTO, BackgroundSize.AUTO,
                    true, true, true, false
                )
            )
        )

        tab("Испытания") {
            content = find(PreFillTab::class).root
        }

        if (AuthorizationModel.user0.level >= 8) {
            tab("Редактор ОИ") {
                content = find(TestItemEditorTab::class).root
            }
        }

        tab("Протоколы") {
            content = find(ProtocolsTab::class).root

            setOnSelectionChanged {
                if ((it.source as Tab).isSelected) protocolsController.fillProtocolsTable()
            }
        }
    }.addClass(Styles.regularLabels)
}
