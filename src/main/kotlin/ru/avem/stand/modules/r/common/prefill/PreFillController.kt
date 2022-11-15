package ru.avem.stand.modules.r.common.prefill

import javafx.geometry.Pos
import ru.avem.stand.head
import ru.avem.stand.modules.i.views.showSaveDialogConfirmation
import ru.avem.stand.modules.r.common.AggregateView
import ru.avem.stand.modules.r.common.authorization.AuthorizationModel
import tornadofx.*
import tornadofx.controlsfx.warningNotification
import java.lang.Thread.sleep
import kotlin.concurrent.thread

var isTestRunning = false
var isCancelAllTests = false

class PreFillController : Controller() {
    val view: PreFillTab by inject()
    private val aggregateView: AggregateView by inject()

    fun toTests() {
        if (PreFillModel.serialNumberProp.value.isEmpty()) {
            warningNotification(
                "Не заполнено поля заводского номера",
                "Заполните поле и повторите",
                Pos.CENTER,
                owner = aggregateView.currentWindow
            )
            return
        }
        if (PreFillModel.testTypeProp.value == null) {
            warningNotification(
                "Не выбран тип ОИ",
                "Выберите тип и повторите",
                Pos.CENTER,
                owner = aggregateView.currentWindow
            )
            return
        }
        if (PreFillModel.selectedTests.isEmpty()) {
            warningNotification(
                "Не выбрано ни одно испытание",
                "Выберите хотя бы одно испытание и повторите",
                Pos.CENTER,
                owner = aggregateView.currentWindow
            )
            return
        }
        if (AuthorizationModel.user0.level < PreFillModel.testTypeProp.value.level) {
            warningNotification(
                "Недостаточно прав доступа",
                "Авторизируйтесь как пользователь с более высоким уровнем доступа и повторите",
                Pos.CENTER,
                owner = aggregateView.currentWindow
            )
            return
        }

        thread {
            PreFillModel.selectedTests.sortBy {
                it.order
            }
            for (selectedTest in PreFillModel.selectedTests) {
                isTestRunning = true
                runLater {
                    head.showTestView(selectedTest)
                }
                while (isTestRunning) {
                    sleep(100)
                }
                if (isCancelAllTests) {
                    isCancelAllTests = false
                    break
                }
            }
            runLater {
                showSaveDialogConfirmation(aggregateView.currentWindow)
            }
        }
    }

    fun toggleAllTests(text: String) {
        if (text == "Выбрать все") {
            view.testsList.items.forEach {
                it.isSelected = true
            }
            view.select()
        } else {
            view.testsList.items.forEach {
                it.isSelected = false
            }
            view.unselect()
        }
    }
}
