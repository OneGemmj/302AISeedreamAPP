package com.seedream.app.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

const val DEFAULT_ENDPOINT = "https://api.302.ai/doubao/images/generations"
const val MODEL_SEEDREAM_5 = "doubao-seedream-5-0-260128"
const val MODEL_SEEDREAM_4_5 = "doubao-seedream-4-5-251128"

enum class ReferenceKind {
    File,
    Url
}

enum class StatusKind {
    Normal,
    Ok,
    Error,
    Muted,
    Warn
}

data class ReferenceImage(
    val id: String = UUID.randomUUID().toString(),
    val kind: ReferenceKind,
    val name: String,
    val value: String,
    val preview: String = value
)

data class ResultImage(
    val id: String = UUID.randomUUID().toString(),
    val src: String,
    val note: String = ""
)

data class SeedreamRequest(
    val model: String,
    val prompt: String,
    val image: List<String> = emptyList(),
    val size: String? = null,
    val seed: Long? = null,
    val responseFormat: String? = "url",
    val watermark: Boolean? = false,
    val stream: Boolean? = false,
    val sequentialImageGeneration: String? = "disabled",
    val maxImages: Int? = null,
    val webSearch: Boolean = false
) {
    fun toJsonObject(): JSONObject {
        val json = JSONObject()
            .put("model", model)
            .put("prompt", prompt)

        if (image.isNotEmpty()) {
            json.put("image", JSONArray().also { array -> image.forEach(array::put) })
        }
        if (!size.isNullOrBlank()) json.put("size", size)
        if (seed != null) json.put("seed", seed)
        if (!responseFormat.isNullOrBlank()) json.put("response_format", responseFormat)
        if (watermark != null) json.put("watermark", watermark)
        if (stream != null) json.put("stream", stream)
        if (!sequentialImageGeneration.isNullOrBlank()) {
            json.put("sequential_image_generation", sequentialImageGeneration)
        }
        if (maxImages != null) {
            json.put("max_images", maxImages)
            json.put(
                "sequential_image_generation_options",
                JSONObject().put("max_images", maxImages)
            )
        }
        if (webSearch && model.startsWith("doubao-seedream-5-")) {
            json.put("tools", JSONArray().put(JSONObject().put("type", "web_search")))
        }
        return json
    }

    fun toJsonString(indentSpaces: Int = 0): String {
        val json = toJsonObject()
        return if (indentSpaces > 0) json.toString(indentSpaces) else json.toString()
    }

    fun toPreviewJsonString(indentSpaces: Int = 2): String {
        val json = toJsonObject()
        if (json.has("image")) {
            val previewImages = JSONArray()
            image.forEach { value -> previewImages.put(value.compactForPreview()) }
            json.put("image", previewImages)
        }
        return json.toString(indentSpaces)
    }

    private fun String.compactForPreview(): String {
        if (startsWith("data:image", ignoreCase = true)) {
            val header = substringBefore(",", missingDelimiterValue = "data:image/*;base64")
            val base64Length = substringAfter(",", missingDelimiterValue = "").length
            return "$header,... <base64 $base64Length chars>"
        }
        return if (length > 300) take(300) + "... <truncated ${length - 300} chars>" else this
    }
}

data class RequestInput(
    val model: String,
    val prompt: String,
    val references: List<ReferenceImage>,
    val size: String,
    val seed: String,
    val responseFormat: String,
    val watermark: String,
    val stream: String,
    val sequentialImageGeneration: String,
    val maxImages: String,
    val webSearch: String
)

fun buildSeedreamRequest(input: RequestInput): SeedreamRequest {
    val seed = input.seed.trim().takeIf { it.isNotEmpty() }?.toLongOrNull()
    val maxImages = input.maxImages.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()

    return SeedreamRequest(
        model = input.model,
        prompt = input.prompt.trim(),
        image = input.references.map { it.value },
        size = input.size.takeIf { it.isNotBlank() },
        seed = seed,
        responseFormat = input.responseFormat.takeIf { it.isNotBlank() },
        watermark = normalizeBoolean(input.watermark),
        stream = normalizeBoolean(input.stream),
        sequentialImageGeneration = input.sequentialImageGeneration.takeIf { it.isNotBlank() },
        maxImages = maxImages,
        webSearch = input.webSearch == "true"
    )
}

fun normalizeBoolean(value: String): Boolean? = when (value) {
    "true" -> true
    "false" -> false
    else -> null
}

fun parseUrlReferenceImages(text: String): List<ReferenceImage> {
    return text
        .lines()
        .map { it.trim() }
        .filter { it.startsWith("http://", ignoreCase = true) || it.startsWith("https://", ignoreCase = true) }
        .distinct()
        .mapIndexed { index, url ->
            ReferenceImage(
                kind = ReferenceKind.Url,
                name = "URL ${index + 1}",
                value = url,
                preview = url
            )
        }
}
