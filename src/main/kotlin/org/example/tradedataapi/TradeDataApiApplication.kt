package org.example.tradedataapi

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.tradedataapi.services.DataReaderService
import org.example.tradedataapi.services.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TradeDataApiApplication {

    @Autowired
    private lateinit var redisService: RedisService

    @Autowired
    private lateinit var dataReaderService: DataReaderService


    private fun updateDataInDatabase() {
        CoroutineScope(Dispatchers.Default).launch {
            val productNamesMap =
                dataReaderService.getProductNamesMapAsynchronously("src/main/resources/data/largeSizeProduct.csv")
            redisService.clearProductNames()
            redisService.writeData(productNamesMap)
            println("All entries have been added to database")
        }
    }

    @PostConstruct
    fun init() {
        updateDataInDatabase()
    }
}

fun main(args: Array<String>) {
    runApplication<TradeDataApiApplication>(*args)
}
