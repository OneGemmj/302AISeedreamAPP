package com.seedream.app.network

import org.json.JSONObject

class SseParser {
    private var buffer = ""

    fun accept(chunk: String): List<JSONObject> {
        buffer += chunk
        val lines = buffer.split('\n')
        buffer = lines.lastOrNull() ?: ""

        return lines.dropLast(1).mapNotNull { raw ->
            val line = raw.trim()
            if (!line.startsWith("data:")) return@mapNotNull null
            val data = line.removePrefix("data:").trim()
            if (data.isBlank() || data == "[DONE]") return@mapNotNull null
            ImageExtractor.parseJson(data) as? JSONObject
        }
    }
}
