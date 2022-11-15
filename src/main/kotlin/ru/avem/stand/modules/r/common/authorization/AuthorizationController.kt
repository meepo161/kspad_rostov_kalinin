package ru.avem.stand.modules.r.common.authorization

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import ru.avem.stand.head
import ru.avem.stand.modules.r.common.AggregateView
import ru.avem.stand.modules.r.storage.Properties
import tornadofx.*
import tornadofx.controlsfx.warningNotification
import java.nio.file.Path
import java.nio.file.Paths

class AuthorizationController : Controller() {
    private val view: AuthorizationView by inject()

    override val configPath: Path = Paths.get("cfg/app.properties")

    fun signIn() {
        if (Properties.standData.isLogin0Enabled) {
            if (isUserValid(AuthorizationModel.user0Prop, AuthorizationModel.user0PswdProp)) {
                val login0 = AuthorizationModel.user0Prop.value.trim()
                if (login0 == "admin" || login0 == "vip" || login0 == "default") {
                    toMainView()
                    return
                } else {
                    val login1 = AuthorizationModel.user1Prop.value.trim()
                    if (login0 == login1) {
                        warningNotification(
                            "Заполнение полей",
                            "${Properties.standData.login1Title} и " +
                                    "${Properties.standData.login2Title} " +
                                    "не могут быть одним и тем же лицом. Заполните верно и повторите.",
                            Pos.BOTTOM_CENTER,
                            owner = view.currentWindow
                        )
                        return
                    }
                    if (Properties.standData.isLogin1Enabled) {
                        if (isUserValid(AuthorizationModel.user1Prop, AuthorizationModel.user1PswdProp)) {
                            toMainView()
                        }
                    }
                }
            }
        } else {
            toMainView()
        }
    }

    private fun isUserValid(login: SimpleStringProperty, password: SimpleStringProperty) = when {
        login.value.trim().isBlank() -> {
            warningNotification(
                "Заполнение полей",
                "Не выбран пользователь из выпадающего списка. Заполните корректно поле и повторите.",
                Pos.BOTTOM_CENTER,
                owner = view.currentWindow
            )
            false
        }
        password.value.trim().isBlank() -> {
            warningNotification(
                "Заполнение полей",
                "Не введён пароль. Заполните корректно поле и повторите.",
                Pos.BOTTOM_CENTER,
                owner = view.currentWindow
            )
            false
        }
        else -> {
            (Properties.users.find { it.login == login.value.trim() && it.password == password.value.trim() } != null)
                .also {
                    if (!it) {
                        warningNotification(
                            "Авторизация",
                            "Невалидные данные пользователя",
                            Pos.BOTTOM_CENTER,
                            owner = view.currentWindow
                        )
                    }
                }
        }
    }

    private fun toMainView() {
        if (Properties.standData.isLogin0Enabled) {
            Properties.users.find { it.login == AuthorizationModel.user0Prop.value.trim() }?.let {
                AuthorizationModel.user0Prop.value = it.toString()
                config[LOGIN_0_KEY] = AuthorizationModel.user0Prop.value
            }
        }

        if (Properties.standData.isLogin1Enabled) {
            Properties.users.find { it.login == AuthorizationModel.user1Prop.value.trim() }?.let {
                AuthorizationModel.user1Prop.value = it.toString()
                config[LOGIN_1_KEY] = AuthorizationModel.user1Prop.value
            }
        }

        head.showView(AggregateView::class)
        view.close()
    }
}
