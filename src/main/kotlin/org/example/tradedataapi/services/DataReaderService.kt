package org.example.tradedataapi.services

import com.opencsv.CSVReader
import kotlinx.coroutines.*
import org.example.tradedataapi.models.InputTradeData
import org.example.tradedataapi.models.ProductName
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStreamReader

@Service
class DataReaderService {

    fun getProductNamesMap(filePath: String): Map<Int, ProductName> {
        val file = File(filePath)
        val rows: List<Array<String>> = CSVReader(InputStreamReader(file.inputStream())).readAll()
        return ProductName.fromSCVRows(rows.subList(1, rows.size))
    }


    suspend fun getProductNamesMapAsynchronously(filePath: String): Map<Int, ProductName> =
        withContext(Dispatchers.IO) {
            val file = File(filePath)
            val rows: List<Array<String>> = CSVReader(InputStreamReader(file.inputStream())).readAll()

            val chunkSize = (rows.size / 16).coerceAtLeast(1)
            val chunks = rows.subList(1, rows.size).chunked(chunkSize)

            val deferredResults: List<Deferred<Map<Int, ProductName>>> = chunks.map { chunk ->
                async {
                    ProductName.fromSCVRows(chunk)
                }
            }

            val results: List<Map<Int, ProductName>> = deferredResults.awaitAll()

            results.reduce { acc, map -> acc + map }
        }

    suspend fun getInputDataListAsynchronously(rows: List<Array<String>>): List<InputTradeData> =
        withContext(Dispatchers.IO) {
            val chunkSize = (rows.size / 16).coerceAtLeast(1)
            val chunks = rows.chunked(chunkSize)

            val deferredResults: List<Deferred<List<InputTradeData>>> = chunks.map { chunk ->
                async {
                    InputTradeData.fromCSVRows(chunk)
                }
            }

            deferredResults.awaitAll().flatten()
        }

}