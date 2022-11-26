package ru.avem.stand.modules.r.tests.psi.ikas

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas10.IKAS10
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas10.IKAS10Model
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep

class IKAS : KSPADTest(view = IKASView::class, reportTemplate = "ikas.xlsx") {
    override val name = "Измерение сопротивления обмотки статора и встроенных термодатчиков при постоянном токе в " +
            "практически холодном состоянии"

    override val testModel = IKASModel

    override fun initVars() {
        super.initVars()
        testModel.specifiedU = PreFillModel.testTypeProp.value.fields["U"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI = PreFillModel.testTypeProp.value.fields["I"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedCos = PreFillModel.testTypeProp.value.fields["COS"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedEfficiency =
            PreFillModel.testTypeProp.value.fields["EFFICIENCY"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedP = PreFillModel.testTypeProp.value.fields["P"]?.value.toDoubleOrDefault(0.0)
        isFirstPlatform = testModel.specifiedP >= 8

        testModel.specifiedRPM = PreFillModel.testTypeProp.value.fields["RPM"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedF = PreFillModel.testTypeProp.value.fields["F"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedScheme = PreFillModel.testTypeProp.value.fields["SCHEME"]?.value ?: "λ"

        testModel.specifiedR = PreFillModel.testTypeProp.value.fields["R_IKAS"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedRtK = PreFillModel.testTypeProp.value.fields["RT_K_IKAS"]?.value.toDoubleOrDefault(0.00393)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.R1.value = ""
            testModel.measuredData.R2.value = ""
            testModel.measuredData.R3.value = ""

            testModel.calculatedData.R1.value = ""
            testModel.calculatedData.R2.value = ""
            testModel.calculatedData.R3.value = ""

            testModel.calculatedR20Data.R1.value = ""
            testModel.calculatedR20Data.R2.value = ""
            testModel.calculatedR20Data.R3.value = ""

            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()
        startPollControlUnit()

        if (isRunning) {
            with(PR61) {
                addCheckableDevice(this)
                CM.startPoll(this, IKAS10Model.STATUS) { value ->
                    testModel.status = value.toInt()
                }
                CM.startPoll(this, IKAS10Model.RESIST_MEAS) { value ->
                    testModel.measuredR = value.toDouble()
                }
            }
        }

        if (isRunning) {
            with(CM.device<TRM202>(PS81)) {
                with(getRegisterById(TRM202Model.T_1)) {
                    readRegister(this)
                    testModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                with(getRegisterById(TRM202Model.T_2)) {
                    readRegister(this)
                    testModel.measuredData.tempTI.value = value.toDouble().autoformat()
                }
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            startMeasuring()
        }
        if (isRunning) {
            calcRs()
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onIKAS()
    }

    private fun startMeasuring() {
        appendMessageToLog(LogTag.INFO, "Начало измерения...")

        if (isRunning) {
            CM.device<IKAS10>(PR61).startMeasuringAB()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R1.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        if (isRunning) {
            CM.device<IKAS10>(PR61).startMeasuringBC()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R2.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        if (isRunning) {
            CM.device<IKAS10>(PR61).startMeasuringCA()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R3.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }
    }

    private fun calcRs() {
        if (testModel.measuredData.R1.value == "Обрыв" ||
            testModel.measuredData.R2.value == "Обрыв" ||
            testModel.measuredData.R3.value == "Обрыв"
        ) {
            testModel.calculatedData.R1.value = "Обрыв"
            testModel.calculatedData.R2.value = "Обрыв"
            testModel.calculatedData.R3.value = "Обрыв"

            testModel.calculatedR20Data.R1.value = "Обрыв"
            testModel.calculatedR20Data.R2.value = "Обрыв"
            testModel.calculatedR20Data.R3.value = "Обрыв"
        } else {
            val r12 = testModel.measuredData.R1.value.toDouble()
            val r23 = testModel.measuredData.R2.value.toDouble()
            val r31 = testModel.measuredData.R3.value.toDouble()

            if (testModel.specifiedScheme == "λ") {
                testModel.calculatedData.R1.value = ((r31 + r12 - r23) / 2.0).autoformat()
                testModel.calculatedData.R2.value = ((r12 + r23 - r31) / 2.0).autoformat()
                testModel.calculatedData.R3.value = ((r23 + r31 - r12) / 2.0).autoformat()
            } else if (testModel.specifiedScheme == "△") {
                testModel.calculatedData.R1.value =
                    (2.0 * r23 * r31 / (r23 + r31 - r12) - (r23 + r31 - r12) / 2.0).autoformat()
                testModel.calculatedData.R2.value =
                    (2.0 * r31 * r12 / (r31 + r12 - r23) - (r31 + r12 - r23) / 2.0).autoformat()
                testModel.calculatedData.R3.value =
                    (2.0 * r12 * r23 / (r12 + r23 - r31) - (r12 + r23 - r31) / 2.0).autoformat()
            }

            val rA = testModel.calculatedData.R1.value.toDouble()
            val rB = testModel.calculatedData.R2.value.toDouble()
            val rC = testModel.calculatedData.R3.value.toDouble()

            val t = testModel.measuredData.tempAmb.value.toDoubleOrDefault(0.0)
            val rtK = testModel.specifiedRtK
            val rtT = 20.0

            testModel.calculatedR20Data.R1.value = (rA / (1 + rtK * (t - rtT))).autoformat()
            testModel.calculatedR20Data.R2.value = (rB / (1 + rtK * (t - rtT))).autoformat()
            testModel.calculatedR20Data.R3.value = (rC / (1 + rtK * (t - rtT))).autoformat()

            testModel.measuredData.averR.value =
                ((testModel.measuredData.R1.value.toDouble() + testModel.measuredData.R2.value.toDouble() + testModel.measuredData.R3.value.toDouble()) / 3.0).autoformat()

            testModel.percentData.R1.value =
                ((testModel.measuredData.R1.value.toDouble() - testModel.measuredData.averR.value.toDouble()) / testModel.measuredData.averR.value.toDouble() * 100).autoformat()
            testModel.percentData.R2.value =
                ((testModel.measuredData.R2.value.toDouble() - testModel.measuredData.averR.value.toDouble()) / testModel.measuredData.averR.value.toDouble() * 100).autoformat()
            testModel.percentData.R3.value =
                ((testModel.measuredData.R3.value.toDouble() - testModel.measuredData.averR.value.toDouble()) / testModel.measuredData.averR.value.toDouble() * 100).autoformat()
        }
    }

    override fun result() {
        super.result()

        when {
            !isSuccess -> {
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
                testModel.measuredData.result.value = "Прервано"
            }

            testModel.measuredData.R1.value == "Обрыв" ||
                    testModel.measuredData.R2.value == "Обрыв" ||
                    testModel.measuredData.R3.value == "Обрыв" -> {
                appendMessageToLog(LogTag.ERROR, "Обрыв")
                testModel.measuredData.result.value = "Обрыв"
            }

            testModel.percentData.R1.value.toDouble() > 2.0 ||
                    testModel.percentData.R2.value.toDouble() > 2.0 ||
                    testModel.percentData.R3.value.toDouble() > 2.0 -> {
                appendMessageToLog(LogTag.ERROR, "Не соответствует. Отклонение превышает 2%")
                testModel.measuredData.result.value = "Не соответствует"
            }

            else -> {
                appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
                testModel.measuredData.result.value = "Соответствует"
            }
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        runLater {
            testModel.progressProperty.value = 0.0
        }
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_IKAS"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["R_SPEC_IKAS"] = testModel.specifiedData.R1.value
        reportFields["R_MEAS_12_IKAS"] = testModel.measuredData.R1.value
        reportFields["R_MEAS_23_IKAS"] = testModel.measuredData.R2.value
        reportFields["R_MEAS_31_IKAS"] = testModel.measuredData.R3.value
        reportFields["R_CALC20_12_IKAS"] = testModel.calculatedR20Data.R1.value
        reportFields["R_CALC20_23_IKAS"] = testModel.calculatedR20Data.R2.value
        reportFields["R_CALC20_31_IKAS"] = testModel.calculatedR20Data.R3.value

        reportFields["TEMP_AMB_IKAS"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_IKAS"] = testModel.measuredData.tempTI.value

        super.saveProtocol()
    }
}
