package ru.avem.stand.modules.i.views

import javafx.geometry.Pos
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import ru.avem.stand.modules.r.storage.Properties
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val regularLabels by cssclass()
        val headerLabels by cssclass()
        val paneBorders by cssclass()
        val paneBoldBorders by cssclass()
        val actionsTitledPane by cssclass()
        val componentControls by cssclass()
        val limitAndUnitLabels by cssclass()
        val componentTitle by cssclass()
        val tabTitle by cssclass()
        val tableLog by cssclass()
        val startupProgressbar by cssclass()

        val lineChart by cssclass()
        val pressure by cssclass()
        val redText by cssclass()
        val greenText by cssclass()
        val earthingSwitchNotTriggered by cssclass()
        val earthingSwitchTriggered by cssclass()
        val medium by cssclass()
        val hard by cssclass()
        val extraHard by cssclass()
        val megaHard by cssclass()
        val stopStart by cssclass()
        val anchorPaneBorders by cssclass()
        val anchorPaneStatusColor by cssclass()
        val roundButton by cssclass()
        val powerButtons by cssclass()
        val kVArPowerButtons by cssclass()
        val tableRowCell by cssclass()
        val vboxTextArea by cssclass()
        val alert by cssclass()

        private var fontMultiplier = 0.0
        var regularFontSize = 0.0
        var titleFontSize = 0.0

        fun initFonts() {
            regularFontSize = Properties.standData.textSize.toDouble()
            fontMultiplier = 18.0 / regularFontSize
            titleFontSize = regularFontSize * fontMultiplier
        }
    }

    init {
        Stylesheet.dialogPane {
            fontSize = regularFontSize.px
        }

        tableLog {
            fontFamily = "Lucida Console"
            cell {
                alignment = Pos.CENTER
            }
            tableRowCell {
                and(selected) {
                    backgroundColor += c("#E8694A")
                }
            }
        }
        tabTitle {
            tab {
                fontWeight = FontWeight.EXTRA_BOLD
                fontSize = titleFontSize.px
            }
        }
        componentTitle {
            title {
                fontSize = regularFontSize.px
                textFill = c("#303030")
                fontWeight = FontWeight.EXTRA_BOLD
            }

            button {
                fontSize = regularFontSize.px
                textFill = c("#404040")
            }

            label {
                fontSize = regularFontSize.px
                textFill = c("#303030")
                fontWeight = FontWeight.EXTRA_BOLD
            }
        }

        componentControls {
            text {
                fontSize = 11.px
            }
        }

        limitAndUnitLabels {
            text {
                fontSize = 10.px
            }
        }

        actionsTitledPane {
            title {
                fontWeight = FontWeight.EXTRA_BOLD
                fontSize = titleFontSize.px
                textFill = c("#303030")
            }
        }

        regularLabels {
            fontSize = regularFontSize.px
            textFill = c("#404040")
        }

        headerLabels {
            fontSize = titleFontSize.px
            fontWeight = FontWeight.EXTRA_BOLD
            textFill = c("#303030")
        }

        tableColumn {
            alignment = Pos.CENTER_LEFT
            fontSize = regularFontSize.px
            textFill = c("#404040")
        }

        checkBox {
            selected {
                mark {
                    backgroundColor += c("black")
                }
            }

            fontSize = regularFontSize.px
            textFill = c("#404040")
        }

        button {
            fontSize = regularFontSize.px
            textFill = c("#404040")
        }

        paneBorders {
            borderColor += box(c("#777"))
        }

        paneBoldBorders {
            borderColor += box(c("#000"))
            borderWidth += box(3.px)
        }

        startupProgressbar {
            track {
                backgroundColor += c("transparent")
            }
            bar {
                prefHeight = 15.px
                minHeight = prefHeight
                backgroundColor += c("white")
            }
        }

        //TODO Maga's styles (переверстать или избавиться)
        tabPane {
            tab {
                focusColor = Paint.valueOf("#00000000") //transparent
            }
        }

        pressure {
            font = loadFont("/font/DSEG7Modern-BoldItalic.ttf", 24.0)!!
            backgroundColor += c("#2e0d08")
            textFill = c("#ff3000")
            fontWeight = FontWeight.EXTRA_BOLD
        }

        redText {
            baseColor = c("#FF0000")
        }

        greenText {
            baseColor = c("#00FF00")
        }

        earthingSwitchNotTriggered {
            baseColor = c(168, 168, 168)
        }

        earthingSwitchTriggered {
            baseColor = c(118, 165, 175)
        }

        medium {
            fontSize = 18.px
            fontWeight = FontWeight.BOLD
        }

        hard {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        megaHard {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        extraHard {
            fontSize = 24.px
            fontWeight = FontWeight.BOLD
        }

        stopStart {
            fontSize = 60.px
            fontWeight = FontWeight.EXTRA_BOLD
            baseColor = c("#333")
        }

        powerButtons {
            fontSize = 18.px
//            baseColor = c("#2178CC")
            baseColor = c("#222")
            prefWidth = 50.px
        }

        kVArPowerButtons {
            fontSize = 18.px
            baseColor = c("#60C3CC")
            prefWidth = 50.px
        }

        anchorPaneBorders {
            borderColor += CssBox(
                top = c("grey"),
                bottom = c("grey"),
                left = c("grey"),
                right = c("grey")
            )
        }

        anchorPaneStatusColor {
            backgroundColor += c("#B4AEBF")
        }

        roundButton {
            backgroundRadius += CssBox(
                top = 30.px,
                bottom = 30.px,
                left = 30.px,
                right = 30.px
            )
        }

        tableColumn {
            alignment = Pos.CENTER
            fontWeight = FontWeight.BOLD
            fontSize = 22.px / 1.6
        }

        tableRowCell {
            cellSize = 50.px / 1.5
        }

        checkBox {
            selected {
                mark {
                    backgroundColor += c("black")
                }
            }
        }

        vboxTextArea {
//            backgroundColor += c("#6696bd")
            backgroundColor += c("#333")
        }

        lineChart {
            chartSeriesLine {
                backgroundColor += c("red")
                stroke = c("red")
            }
            chartLineSymbol {
                backgroundColor += c("red")
            }
        }
    }
}
