package ru.avem.stand.modules.r.common.settings.main

import javafx.stage.FileChooser
import javafx.util.StringConverter
import org.controlsfx.control.Notifications
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class MainSettingsTab : View("Основные") {
    val model = MainSettingsViewModel()

    private val pathConverter = object : StringConverter<Path>() {
        override fun toString(p0: Path?) = p0.toString()
        override fun fromString(p0: String?) = Paths.get(p0!!)
    }

    override val root = scrollpane {

        form {
            prefWidth = 1000.0
            spacing = 16.0

            fieldset("Общие настройки стенда") {
                spacing = 8.0
                field("Режим киоска") {
                    checkbox().bind(model.kioskMode)
                }
                field("На весь экран") {
                    checkbox().disableWhen(model.kioskMode).bind(model.fullscreen)
                }
                field("Запрашивать подтверждение выхода") {
                    checkbox().bind(model.exitConfirmation)
                }

                fieldset("Размеры окна") {
                    field("Ширина") {
                        textfield().stripNonNumeric().doubleBinding(model.width) {
                            it!!.toDouble()
                        }
                    }.disableWhen(model.fullscreen)
                    field("Высота") {
                        textfield().stripNonNumeric().doubleBinding(model.height) {
                            it!!.toDouble()
                        }
                    }.disableWhen(model.fullscreen)
                    field("Зафиксировать размер окна во время работы") {
                        checkbox().disableWhen(model.kioskMode).bind(model.sizeLocked)
                    }
                }.disableWhen(model.kioskMode)

                fieldset("Сплешскрин") {
                    field("Путь") {
                        textfield {
                            isEditable = false
                            bind(model.splashPic, converter = pathConverter)
                        }
                        button("Обзор") {
                            action {
                                Notifications
                                    .create()
                                    .title("Подсказка")
                                    .text("Файл должен быть изображением с размерами 1700х900")
                                    .showInformation()
                                chooseFile(
                                    title = "Выберите файл",
                                    filters = arrayOf(FileChooser.ExtensionFilter("Изображение", "*.png"))
                                ).apply {
                                    if (this.isEmpty()) {
                                        return@action
                                    }
                                    //TODO Image validation
                                    model.splashPic.value = Paths.get(this.first().toURI())
                                }
                            }
                        }
                    }
                    field("Время демонстрации, мс.") {
                        textfield {
                            bind(model.splashTime)
                        }.stripNonNumeric()
                    }
                }

                fieldset("Иконка приложения") {
                    field("Путь") {
                        textfield {
                            isEditable = false
                            bind(model.icon, converter = pathConverter)
                        }
                        button("Обзор") {
                            action {
                                chooseFile(
                                    title = "Выберите файл",
                                    filters = arrayOf(FileChooser.ExtensionFilter("Изображение", "*.png"))
                                ).apply {
                                    if (this.isEmpty()) {
                                        return@action
                                    }
                                    model.icon.value = Paths.get(this.first().toURI())
                                }
                            }
                        }
                    }
                }

                field("Размер текста") {
                    textfield().bind(model.textSize)
                }

                fieldset("О стенде") {
                    field("Наименование проекта") {
                        textfield().bind(model.project)
                    }
                    field("Заказчик") {
                        textfield().bind(model.customer)
                    }
                    field("Город заказчика") {
                        textfield().bind(model.customerPlace)
                    }
                    field("Изготовитель") {
                        textfield().bind(model.manufacture)
                    }
                    field("Место (город) изготовителя") {
                        textfield().bind(model.manufacturePlace)
                    }
                    field("Дата изготовления") {
                        textfield().bind(model.manufactureDate)
                    }
                    field("Полное название") {
                        textfield().bind(model.titleFull)
                    }
                    field("Сокращенное название") {
                        textfield().bind(model.titleShort)
                    }
                    field("Серийный номер") {
                        textfield().bind(model.serialNumber)
                    }
                    field("Идентификатор (версия) сборки") {
                        textfield().bind(model.hardwareID)
                    }
                    field("Идентификатор (версия) конфигурационных файлов") {
                        textfield().bind(model.softwareID)
                    }
                }

                fieldset("Авторизация") {
                    field("Нужна ли основная авторизация") {
                        checkbox().bind(model.isLogin0Enabled)
                    }
                    fieldset {
                        field("Название первого пользователя") {
                            textfield().bind(model.login1Title)
                        }
                        field("Минимальный уровень пользователей, отображаемых в списке") {
                            combobox(model.login1Level, (1..8).toList()) {
                                useMaxWidth = true
                            }
                        }
                    }.enableWhen(model.isLogin0Enabled)
                    field("Нужна ли дополнительная авторизация") {
                        checkbox().bind(model.isLogin1Enabled)
                    }
                    fieldset {
                        field("Название второго пользователя") {
                            textfield().bind(model.login2Title)
                        }
                        field("Минимальный уровень пользователей, отображаемых в списке") {
                            combobox(model.login2Level, (1..8).toList()) {
                                useMaxWidth = true
                            }
                        }
                    }.enableWhen(model.isLogin1Enabled)
                }
            }
        }
    }
}
