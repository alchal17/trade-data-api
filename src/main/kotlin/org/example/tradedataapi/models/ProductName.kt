package org.example.tradedataapi.models

@JvmInline
value class ProductName(val value: String) {
    companion object {
        inline fun fromCSVRow(row: Array<String>): Pair<Int, ProductName> {
            return row[0].toInt() to ProductName(row[1])
        }

        fun fromSCVRows(rows: List<Array<String>>): Map<Int, ProductName> {
            return rows.map { fromCSVRow(it) }.associate { Pair(it.first, it.second) }
        }
    }
}