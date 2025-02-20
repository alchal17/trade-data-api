package org.example.tradedataapi.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class OutputTradeData(
    val date: LocalDate,
    val productName: ProductName,
    val currency: String,
    val price: Float
) {
    val formattedDate: String
        get() = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

    companion object {
        fun fromInputData(inputTradeData: InputTradeData, productName: ProductName): OutputTradeData {
            return OutputTradeData(
                date = inputTradeData.date,
                productName = productName,
                currency = inputTradeData.currency,
                price = inputTradeData.price
            )
        }
    }
}
