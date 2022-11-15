package ru.avem.stand.modules.i.views

import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.stage.Stage
import ru.avem.stand.modules.r.storage.Properties
import tornadofx.*
import tornadofx.FX.Companion.primaryStage
import java.nio.file.Files
import kotlin.concurrent.thread
import kotlin.reflect.KClass

object TFXViewManager {
    init {
        thread {
            launch<TFXApp>()
        }
    }

    lateinit var mainBackground: Background

    fun openModal(view: KClass<out View>) {
        runLater {
            find(view).openModal(escapeClosesWindow = false)
        }
    }

    class TFXApp : App(AppStartView::class) {
        override fun start(stage: Stage) {
            super.start(stage)
            Styles.initFonts()
            importStylesheet(Styles::class)

            if (Files.exists(Properties.standData.icon) &&
                !Files.isDirectory(Properties.standData.icon)
            ) {
                primaryStage.icons.add(Image(Files.newInputStream(Properties.standData.icon)))
            } else {
                primaryStage.icons.add(Image(this::class.java.getResourceAsStream("/icon.png")))
            }

            stage.opacity = 0.0

            mainBackground = Background(
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
        }
    }

    class AppStartView : View("КСПАД") {
        override fun onDock() {
            super.onDock()
            currentWindow?.setOnCloseRequest {
                it.consume()
            }
        }

        override val root = label("КСПАД") // TODO взять из более общего места
    }
}
