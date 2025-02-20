package org.example.tradedataapi.services

import io.github.cdimascio.dotenv.dotenv
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.example.tradedataapi.models.ProductName
import org.springframework.stereotype.Service

@Service
class RedisService {


    private fun getClient(): RedisClient {
        val dotenv = dotenv()
        val redisHost = dotenv["REDIS_HOST"]
        val redisPort = dotenv["REDIS_PORT"]
        val redisPassword = dotenv["REDIS_PASSWORD"]
        val client = RedisClient.create("redis://$redisPassword@$redisHost:$redisPort")
        return client
    }

    private fun getConnection(client: RedisClient): StatefulRedisConnection<String, String> {
        return client.connect()
    }

    private val client = getClient()
    private val connection = getConnection(client)
    private val syncCommands: RedisCommands<String, String> = connection.sync()

    fun writeData(data: Map<Int, ProductName>) {
        val stringMap = data.mapKeys { it.key.toString() }.mapValues { it.value.value }
        syncCommands.hmset("product_names", stringMap)
    }

    fun getData(id: Int): ProductName {
        val result = syncCommands.hget("product_names", id.toString())
        return ProductName(result ?: "Missing Product Name")
    }

    fun getUniqueDataByKeys(ids: List<Int>): Map<Int, ProductName> {
        val keys = ids.toSet().map { it.toString() }
        val values = syncCommands.hmget("product_names", *keys.toTypedArray())

        val keyValuePairs = values.associate { it.key.toInt() to ProductName(it.getValueOrElse("Missing Product Name")) }
        return keyValuePairs

    }

    fun clearProductNames() {
        syncCommands.del("product_names")
    }

    fun closeConnection() {
        connection.close()
        client.shutdown()
    }

}