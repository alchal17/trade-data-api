package org.example.tradedataapi.infrastructure.shutdown

import jakarta.annotation.PreDestroy
import org.example.tradedataapi.services.RedisService
import org.springframework.stereotype.Component

@Component
class ShutdownHandler(private val redisService: RedisService) {

    @PreDestroy
    fun shutdown() {
        println("Shutting down")
        redisService.closeConnection()
    }
}