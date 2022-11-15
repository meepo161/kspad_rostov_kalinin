package ru.avem.stand.modules.r.storage.protocol

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.stand.modules.r.storage.database.entities.Report
import ru.avem.stand.utils.autoformat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat

fun saveProtocolsAsWorkbook(reports: List<Report>, reportPathString: String, targetPath: File? = null): Path {
    val reportDir =
        Files.createDirectories(Paths.get("report/${SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())}"))
    val resultBook = targetPath ?: Paths.get(reportDir.toString(), "${System.currentTimeMillis()}.xlsx").toFile()
    val templateStream = Report::class.java.getResourceAsStream("/reports/${reportPathString}")
    copyFileFromStream(templateStream, resultBook)

    val fields = reports.map(Report::filledFields).flatten()

    XSSFWorkbook(resultBook).use { workBook ->
        val sheet = workBook.getSheetAt(0)
        sheet.rowIterator().forEach { row ->
            row.cellIterator().forEach { cell ->
                if (cell != null && (cell.cellType == CellType.STRING)) {
                    fields.find { cell.stringCellValue.contains(it.key) }
                        ?.let {
                            val value = cell.stringCellValue.replace(it.key, it.value.autoformat())
                            cell.setCellValue(value.toDoubleOrNull() ?: value)
                        } ?: if (cell.stringCellValue.contains("$")) {
                        cell.setCellValue("")
                    }
                }
            }
        }
        val outStream = ByteArrayOutputStream()
        workBook.write(outStream)
        outStream.close()
    }

    return Paths.get(resultBook.absolutePath)
}

private fun Cell.setCellValue(any: Any) {
    when (any) {
        is Double -> setCellValue(any)
        else -> setCellValue(any.toString())
    }
}

fun saveProtocolAsWorkbook(report: Report, targetPath: File? = null): Path {
    val reportDir =
        Files.createDirectories(Paths.get("/report/${SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())}"))
    val resultBook = targetPath ?: Paths.get(reportDir.toString(), "${System.currentTimeMillis()}.xlsx").toFile()
    val templateStream = Report::class.java.getResourceAsStream("/reports/${report.template}")
    copyFileFromStream(templateStream, resultBook)

    val fields = report.filledFields

    XSSFWorkbook(resultBook).use { workBook ->
        val sheet = workBook.getSheetAt(0)
        sheet.rowIterator().forEach { row ->
            row.cellIterator().forEach { cell ->
                if (cell != null && (cell.cellType == CellType.STRING)) {
                    fields.find { cell.stringCellValue.contains(it.key) }?.let {
                        val value = cell.stringCellValue.replace(it.key, it.value.autoformat())
                        cell.setCellValue(value.toDoubleOrNull() ?: value)
                    } ?: if (cell.stringCellValue.contains("$")) {
                        cell.setCellValue("")
                    }
                }
            }
        }
        val outStream = ByteArrayOutputStream()
        workBook.write(outStream)
        outStream.close()
    }

    return Paths.get(resultBook.absolutePath)
}

private fun copyFileFromStream(_inputStream: InputStream, dest: File) {
    _inputStream.use { inputStream ->
        val fileOutputStream = FileOutputStream(dest)
        val buffer = ByteArray(1024)
        var length = inputStream.read(buffer)
        while (length > 0) {
            fileOutputStream.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }
    }
}
