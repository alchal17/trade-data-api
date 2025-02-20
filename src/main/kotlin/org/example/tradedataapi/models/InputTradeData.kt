package org.example.tradedataapi.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class InputTradeData(val date: LocalDate, val productId: Int, val currency: String, val price: Float) {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        inline fun fromSCVRow(row: Array<String>): InputTradeData? {
            return try {
                InputTradeData(
                    date = LocalDate.parse(row[0], formatter),
                    productId = row[1].toInt(),
                    currency = row[2],
                    price = row[3].toFloat()
                )
            } catch (e: Exception) {
                println("Error parsing row: ${row.joinToString()} - ${e.message}")
                null
            }
        }
        fun fromCSVRows(rows: List<Array<String>>): List<InputTradeData> {
            return rows.mapNotNull { fromSCVRow(it) }
        }
    }
}