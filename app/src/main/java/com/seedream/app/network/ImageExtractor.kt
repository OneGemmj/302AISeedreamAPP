package com.seedream.app.network

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

object ImageExtractor {
    private val imageUrlRegex = Regex(
        pattern = "^https?://.+\\.(png|jpe?g|webp|gif|bmp|svg)(\\?.*)?$",
        options = setOf(RegexOption.IGNORE_CASE)
    )
    private val signedImageHintRegex = Regex(
        pattern = "image|img|photo|picture|seedream|doubao",
        options = setOf(RegexOption.IGNORE_CASE)
    )
    private val imageKeyRegex = Regex(
        pattern = "url|image|img|photo|picture|src",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    fun parseJson(text: String): Any? {
        return runCatching { JSONTokener(text).nextValue() }.getOrNull()
    }

    fun findImageUrlsDeep(input: Any?, out: MutableList<String> = mutableListOf()): List<String> {
        val seen = out.toMutableSet()
        walk(input, out, seen)
        return out
    }

    fun directImagesFromResponse(input: Any?, responseFormat: String?): List<String> {
        val json = input as? JSONObject ?: return emptyList()
        val data = json.optJSONArray("data") ?: return emptyList()
        val images = mutableListOf<String>()
        for (index in 0 until data.length()) {
            val item = data.optJSONObject(index) ?: continue
            when {
                responseFormat == "b64_json" && item.optString("b64_json").isNotBlank() -> {
                    images += "data:image/jpeg;base64,${item.optString("b64_json")}"
                }
                item.optString("url").isNotBlank() -> images += item.optString("url")
            }
        }
        return images
    }

    private fun walk(input: Any?, out: MutableList<String>, seen: MutableSet<String>, parentKey: String? = null) {
        when (input) {
            null, JSONObject.NULL -> Unit
            is String -> {
                val value = input.trim()
                val keyLooksLikeImage = parentKey?.let { imageKeyRegex.containsMatchIn(it) } == true
                val looksLikeImage = imageUrlRegex.matches(value) ||
                    (value.startsWith("http://", true) || value.startsWith("https://", true)) &&
                    signedImageHintRegex.containsMatchIn(value)
                if ((looksLikeImage || (keyLooksLikeImage && value.startsWith("http", true))) && seen.add(value)) {
                    out += value
                }
            }
            is JSONObject -> {
                val keys = input.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    walk(input.opt(key), out, seen, key)
                }
            }
            is JSONArray -> {
                for (index in 0 until input.length()) {
                    walk(input.opt(index), out, seen, parentKey)
                }
            }
        }
    }
}
