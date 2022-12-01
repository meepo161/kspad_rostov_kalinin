package ru.avem.stand.modules.i.views

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.common.prefill.isTestRunning
import tornadofx.*
import tornadofx.FX.Companion.primaryStage
import java.lang.Thread.sleep
import java.nio.file.Paths
import kotlin.system.exitProcess

fun showExitConfirmation(windowEvent: WindowEvent, currentWindow: Window?) {
    confirmation(
        "Выход",
        "Вы действительно хотите выйти?",
        ButtonType("Да"),
        ButtonType("Нет"),
        title = "Выход",
        owner = currentWindow
    ) { buttonType ->
        when (buttonType.text) {
            "Да" -> exitProcess(0)
            "Нет" -> windowEvent.consume()
        }
    }
}

fun showCancelConfirmation(windowEvent: WindowEvent, currentWindow: Window?, currentStage: Stage?, test: Test) {
    if (test.isFinished) {
        currentStage?.close()
        isTestRunning = false
    } else {
        confirmation(
            "Отмена",
            "Вы действительно хотите остановить и отменить испытание?",
            ButtonType("Да"),
            ButtonType("Нет"),
            title = "Отмена",
            owner = currentWindow
        ) { buttonType ->
            when (buttonType.text) {
                "Да" -> {
                    test.stop() // TODO окно дождаться конца
                    currentStage?.close()
                    isTestRunning = false
                }
                "Нет" -> windowEvent.consume()
            }
        }
    }
}

//fun showSaveDialogConfirmation(currentWindow: Window?) { // TODO добавить опцию
//    confirmation(
//        "Конец",
//        "Все испытания завершены либо отменены. Сохранить протокол?",
//        ButtonType("Да"),
//        ButtonType("Нет"),
//        title = "Конец",
//        owner = currentWindow
//    ) { buttonType ->
//        when (buttonType.text) {
//            "Да" -> {}
//            "Нет" -> {}
//        }
//    }
//}

fun showSaveDialogConfirmation(currentWindow: Window?) {
    confirmation(
        "Конец",
        "Все выбранные испытания завершены/отменены. Протоколы сохранены",
        ButtonType("Ок"),
        title = "Конец",
        owner = currentWindow
    ) { buttonType ->
        when (buttonType.text) {
            "Да" -> {
            }
        }
    }
}

fun showDialogConfirmation(currentWindow: Window?) {
    confirmation(
        "Внимание",
        "Нажмите Ок и проверните двигатель на 360 градусов",
        ButtonType("Ок"),
        title = "Внимание",
        owner = currentWindow
    ) { buttonType ->
        when (buttonType.text) {
            "Ок" -> {
            }
        }
    }
}


fun showTwoWayDialog(
    title: String,
    text: String,
    way1Title: String,
    way2Title: String,
    way1: () -> Unit,
    way2: () -> Unit,
    timeout: Long,
    breakCondition: () -> Boolean,
    currentWindow: Window
) {
    val initTime = System.currentTimeMillis()

    var isDialogOpened = true

    runLater {
        warning(
            title,
            text,
            ButtonType(way1Title),
            ButtonType(way2Title),
            owner = currentWindow
        ) { buttonType ->
            when (buttonType.text) {
                way1Title -> way1()
                way2Title -> way2()
            }
            isDialogOpened = false
        }
    }

    while (isDialogOpened && !breakCondition()) {
        sleep(10)
        val elapsedTime = System.currentTimeMillis() - initTime

        if (elapsedTime > timeout) {
//            if (isDialogOpened) { TODO
//                runLater { alert.close() }
//            }
            throw Exception("Время ожидания диалога превышено")
        }
    }
}

fun showSetDialog(
    timeout: Long,
    title: String,
    text: String,
    isDialogOpened: () -> Boolean,
    breakCondition: () -> Boolean
) {
    val initTime = System.currentTimeMillis()

    runLater {
        TextInputDialog("0").apply {
            this.title = title
            headerText = text
            contentText = "Задать"
        }.also {
            (it.dialogPane.scene.window as Stage).icons.add(primaryStage.icons[0])
            it.dialogPane.style {
                fontSize = Styles.titleFontSize.px
            }
            it.showAndWait().ifPresent { value ->
                error("TODO $value")
            }
        }
    }

    while (isDialogOpened() && !breakCondition()) {
        sleep(10)
        val elapsedTime = System.currentTimeMillis() - initTime

        if (elapsedTime > timeout) {
            throw Exception("Время ожидания диалога превышено")
        }
    }
}

fun showOKDialog(
    timeout: Long,
    title: String,
    text: String,
    isDialogOpened: () -> Boolean,
    breakCondition: () -> Boolean
) {
    val initTime = System.currentTimeMillis()

    runLater {
        Alert(Alert.AlertType.NONE, title, ButtonType.OK).apply {
            this.title = title
            contentText = text
        }.also {
            (it.dialogPane.scene.window as Stage).icons.add(primaryStage.icons[0])
            it.dialogPane.style {
                fontSize = Styles.titleFontSize.px
            }
            it.showAndWait().ifPresent { }
        }
    }

    while (isDialogOpened() && !breakCondition()) {
        sleep(10)
        val elapsedTime = System.currentTimeMillis() - initTime

        if (elapsedTime > timeout) {
            throw Exception("Время ожидания диалога превышено")
        }
    }
}

fun showImageDialog(
    timeout: Long,
    title: String,
    text: String,
    imagePath: String,
    isDialogOpened: () -> Boolean,
    breakCondition: () -> Boolean
) {
    val initTime = System.currentTimeMillis()

    runLater {
        Alert(Alert.AlertType.NONE, title, ButtonType.OK).apply {
            this.title = title
            contentText = text
            graphic = ImageView(Image(Paths.get(imagePath).toFile().inputStream()))
        }.also {
            (it.dialogPane.scene.window as Stage).icons.add(primaryStage.icons[0])
            it.dialogPane.style {
                fontSize = Styles.titleFontSize.px
            }
            it.showAndWait().ifPresent { }
        }
    }

    while (isDialogOpened() && !breakCondition()) {
        sleep(10)
        val elapsedTime = System.currentTimeMillis() - initTime

        if (elapsedTime > timeout) {
            throw Exception("Время ожидания диалога превышено")
        }
    }
}
