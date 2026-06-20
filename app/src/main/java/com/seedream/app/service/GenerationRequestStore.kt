package com.seedream.app.service

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class PendingGenerationRequest(
    val payloadJson: String,
    val apiKey: String,
    val endpoint: String
)

object GenerationRequestStore {
    private val requests = ConcurrentHashMap<String, PendingGenerationRequest>()

    fun put(payloadJson: String, apiKey: String, endpoint: String): String {
        val id = UUID.randomUUID().toString()
        requests[id] = PendingGenerationRequest(payloadJson, apiKey, endpoint)
        return id
    }

    fun take(id: String): PendingGenerationRequest? = requests.remove(id)
}
