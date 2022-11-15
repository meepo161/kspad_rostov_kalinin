package ru.avem.stand.modules.r.common.authorization

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.text.FontWeight
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.ViewModule
import ru.avem.stand.modules.r.storage.Properties
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

class AuthorizationView(title: String = "Авторизация") : ViewModule(title, showOnStart = true) {
    private val controller: AuthorizationController by inject()

    override val configPath: Path = Paths.get("cfg/app.properties")

    override fun onBeforeShow() {
        super.onBeforeShow()
        currentWindow?.setOnCloseRequest {
            exitProcess(0)
        }
    }

    override val root = anchorpane {
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

        vbox(spacing = 32.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }

            alignment = Pos.CENTER

            label("Авторизация") {
                style {
                    fontSize = 18.px
                    fontWeight = FontWeight.BOLD
                }
            }

            hbox(spacing = 64.0) {
                alignment = Pos.CENTER

                if (Properties.standData.isLogin0Enabled) {
                    hbox(spacing = 16) {
                        vbox(spacing = 19.0) {
                            alignment = Pos.CENTER_LEFT
                            label(Properties.standData.login1Title)
                            label("Пароль")
                        }

                        vbox(spacing = 9.0) {
                            combobox<String> {
                                prefWidth = 200.0
                                isEditable = true
                                items =
                                    Properties.users.filter { it.level >= Properties.standData.login1Level && it.level < 9 }
                                        .map(Any::toString).observable()
                            }.bind(AuthorizationModel.user0Prop)

                            passwordfield {
                                prefWidth = 200.0
                            }.bind(AuthorizationModel.user0PswdProp)
                        }
                    }
                }

                if (Properties.standData.isLogin1Enabled) {
                    hbox(spacing = 16) {
                        vbox(spacing = 19.0) {
                            alignment = Pos.CENTER_LEFT
                            label(Properties.standData.login2Title)
                            label("Пароль")
                        }

                        vbox(spacing = 9.0) {
                            combobox<String> {
                                prefWidth = 200.0
                                isEditable = true
                                items =
                                    Properties.users.filter { it.level >= Properties.standData.login2Level && it.level < 9 }
                                        .map(Any::toString).observable()
                            }.bind(AuthorizationModel.user1Prop)

                            passwordfield {
                                prefWidth = 200.0
                            }.bind(AuthorizationModel.user1PswdProp)
                        }
                    }
                }
            }

            button("Вход") {
                prefWidth = 100.0

                isDefaultButton = true

                action {
                    controller.signIn()
                }
            }
        }.addClass(Styles.regularLabels)
    }
}
