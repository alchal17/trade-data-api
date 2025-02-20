package org.example.tradedataapi

import kotlinx.coroutines.test.runTest
import org.example.tradedataapi.models.InputTradeData
import org.example.tradedataapi.models.ProductName
import org.example.tradedataapi.services.DataReaderService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class TradeDataApiApplicationTests {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")


    @Autowired
    private lateinit var dataReaderService: DataReaderService


    @Test
    fun `test valid InputTradeData transformation from row`() {
        val row = arrayOf("20160101", "1", "EUR", "10.0")
        val expected = InputTradeData(
            date = LocalDate.parse("20160101", formatter),
            productId = 1,
            currency = "EUR",
            price = 10.0f
        )
        assertEquals(expected, InputTradeData.fromSCVRow(row))
    }

    @Test
    fun `test invalid InputTradeData transformation from row`() {
        val row = arrayOf("", "1", "EUR", "10.0")
        assertEquals(null, InputTradeData.fromSCVRow(row))
    }

    @Test
    fun `test data from received CSV asynchronously`() = runTest {
        val data: Map<Int, ProductName> =
            dataReaderService.getProductNamesMapAsynchronously("src/main/resources/data/testProductNames.csv")
        val expected: Map<Int, ProductName> = mapOf(
            1 to ProductName("Commodity Swaps 1"),
            2 to ProductName("Commodity Swaps"),
            3 to ProductName("FX Forward"),
            4 to ProductName("Government Bonds Domestic"),
            5 to ProductName("Convertible Bonds Domestic"),
            6 to ProductName("Corporate Bonds International"),
            7 to ProductName("Interest Rate Futures"),
            8 to ProductName("Overnight Index Swaps"),
            9 to ProductName("Credit Default Swaps"),
            10 to ProductName("Inflation-Linked Bonds"),
            14 to ProductName("Government Bonds Domestic"),
            15 to ProductName("Convertible Bonds Domestic"),
            16 to ProductName("Corporate Bonds International"),
            17 to ProductName("Interest Rate Futures"),
            18 to ProductName("Overnight Index Swaps"),
            19 to ProductName("Credit Default Swaps"),
            20 to ProductName("Inflation-Linked Bonds"),
            21 to ProductName("Equity Futures"),
            22 to ProductName("Commodity Swaps"),
            26 to ProductName("Corporate Bonds International"),
            27 to ProductName("Interest Rate Futures"),
            28 to ProductName("Overnight Index Swaps"),
            29 to ProductName("Credit Default Swaps"),
            30 to ProductName("Inflation-Linked Bonds"),
            31 to ProductName("Equity Futures"),
            32 to ProductName("Commodity Swaps"),
            33 to ProductName("FX Forward"),
            34 to ProductName("Government Bonds Domestic")
        )
        assertEquals(expected, data)
    }

    @Test
    fun `test data from received CSV synchronously`() {
        val data: Map<Int, ProductName> =
            dataReaderService.getProductNamesMap("src/main/resources/data/testProductNames.csv")
        val expected: Map<Int, ProductName> = mapOf(
            1 to ProductName("Commodity Swaps 1"),
            2 to ProductName("Commodity Swaps"),
            3 to ProductName("FX Forward"),
            4 to ProductName("Government Bonds Domestic"),
            5 to ProductName("Convertible Bonds Domestic"),
            6 to ProductName("Corporate Bonds International"),
            7 to ProductName("Interest Rate Futures"),
            8 to ProductName("Overnight Index Swaps"),
            9 to ProductName("Credit Default Swaps"),
            10 to ProductName("Inflation-Linked Bonds"),
            14 to ProductName("Government Bonds Domestic"),
            15 to ProductName("Convertible Bonds Domestic"),
            16 to ProductName("Corporate Bonds International"),
            17 to ProductName("Interest Rate Futures"),
            18 to ProductName("Overnight Index Swaps"),
            19 to ProductName("Credit Default Swaps"),
            20 to ProductName("Inflation-Linked Bonds"),
            21 to ProductName("Equity Futures"),
            22 to ProductName("Commodity Swaps"),
            26 to ProductName("Corporate Bonds International"),
            27 to ProductName("Interest Rate Futures"),
            28 to ProductName("Overnight Index Swaps"),
            29 to ProductName("Credit Default Swaps"),
            30 to ProductName("Inflation-Linked Bonds"),
            31 to ProductName("Equity Futures"),
            32 to ProductName("Commodity Swaps"),
            33 to ProductName("FX Forward"),
            34 to ProductName("Government Bonds Domestic")
        )
        assertEquals(expected, data)
    }

}
