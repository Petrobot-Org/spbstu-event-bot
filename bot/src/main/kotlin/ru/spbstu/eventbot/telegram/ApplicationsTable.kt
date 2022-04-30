package ru.spbstu.eventbot.telegram

import io.github.evanrupert.excelkt.Sheet
import io.github.evanrupert.excelkt.Workbook
import io.github.evanrupert.excelkt.workbook
import ru.spbstu.eventbot.domain.entities.Application
import java.io.ByteArrayOutputStream

fun createApplicationsXlsx(applications: List<Application>): ByteArray {
    val outputStream = ByteArrayOutputStream()
    workbook(applications).xssfWorkbook.write(outputStream)
    return outputStream.toByteArray()
}

private fun workbook(applications: List<Application>): Workbook {
    return workbook {
        sheet {
            header()
            applications.forEach { application ->
                row {
                    cell(application.student.fullName)
                    cell(application.student.group)
                    cell(application.student.email)
                    application.additionalInfo?.let { cell(it) }
                }
            }
            xssfSheet.autoSizeColumn(0)
            xssfSheet.autoSizeColumn(1)
            xssfSheet.autoSizeColumn(2)
            xssfSheet.setColumnWidth(3, 100 * 256)
        }
    }
}

private fun Sheet.header() {
    val headings = listOf(
        Strings.FullNameTableHeader,
        Strings.GroupTableHeader,
        Strings.EmailTableHeader,
        Strings.AdditionalInfoTableHeader
    )
    val style = createCellStyle {
        setFont(
            createFont {
                bold = true
            }
        )
    }
    row(style) {
        headings.forEach { cell(it) }
    }
}
