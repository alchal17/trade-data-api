package org.example.tradedataapi.controllers

import com.opencsv.CSVReader
import jakarta.servlet.http.HttpServletResponse
import org.example.tradedataapi.models.OutputTradeData
import org.example.tradedataapi.services.DataReaderService
import org.example.tradedataapi.services.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/v1")
class DataController {
    @Autowired
    private lateinit var dataReaderService: DataReaderService

    @Autowired
    private lateinit var redisService: RedisService


    @GetMapping("/enrich_json")
    suspend fun enrichJson(@RequestParam("file") file: MultipartFile): List<Map<String, Any>> {
        val reader = CSVReader(InputStreamReader(file.inputStream))
        val lines = reader.readAll()
        val inputTradeDataList = dataReaderService.getInputDataListAsynchronously(lines.subList(1, lines.size))
        val ids = inputTradeDataList.map { it.productId }
        val productNameMap = redisService.getUniqueDataByKeys(ids)

        val outputTradeDataList = inputTradeDataList.map {
            OutputTradeData.fromInputData(
                it,
                productNameMap[it.productId] ?: throw IllegalArgumentException("Product name not found")
            )
        }
        return outputTradeDataList.map {
            mapOf(
                "date" to it.formattedDate,
                "productName" to it.productName.value,
                "currency" to it.currency,
                "price" to it.price
            )
        }
    }


    @GetMapping("/enrich")
    suspend fun enrichData(@RequestParam("file") file: MultipartFile, response: HttpServletResponse) {
        response.contentType = "text/csv"
        response.setHeader("Content-Disposition", "attachment; filename=output.csv")

        file.inputStream.bufferedReader().use { reader ->
            val csvReader = CSVReader(reader)
            val writer = PrintWriter(OutputStreamWriter(response.outputStream, StandardCharsets.UTF_8))

            writer.println("date,productName,currency,price")

            csvReader.readNext()

            val batchSize = 1000
            val buffer = mutableListOf<Array<String>>()

            while (true) {
                val row = csvReader.readNext() ?: break
                buffer.add(row)

                if (buffer.size >= batchSize) {
                    processAndWrite(buffer, writer)
                    buffer.clear()
                }
            }

            if (buffer.isNotEmpty()) {
                processAndWrite(buffer, writer)
            }

            writer.flush()
            writer.close()
        }
    }

    private suspend fun processAndWrite(
        rows: List<Array<String>>,
        writer: PrintWriter
    ) {
        val inputTradeDataList = dataReaderService.getInputDataListAsynchronously(rows)
        val ids = inputTradeDataList.map { it.productId }
        val productNameMap = redisService.getUniqueDataByKeys(ids)

        val outputTradeDataList = inputTradeDataList.map {
            OutputTradeData.fromInputData(
                it,
                productNameMap[it.productId] ?: throw IllegalArgumentException("Product name not found")
            )
        }

        for (tradeData in outputTradeDataList) {
            writer.println(
                "${tradeData.date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}," +
                        "${tradeData.productName.value}," +
                        "${tradeData.currency}," +
                        "${tradeData.price.toString().toDouble()}"
            )
        }
    }

}