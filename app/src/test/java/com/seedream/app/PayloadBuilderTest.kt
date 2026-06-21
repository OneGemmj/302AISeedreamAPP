package com.seedream.app

import com.seedream.app.model.MODEL_SEEDREAM_4_5
import com.seedream.app.model.MODEL_SEEDREAM_5
import com.seedream.app.model.ReferenceImage
import com.seedream.app.model.ReferenceKind
import com.seedream.app.model.RequestInput
import com.seedream.app.model.buildSeedreamRequest
import com.seedream.app.model.normalizeBoolean
import com.seedream.app.model.parseUrlReferenceImages
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PayloadBuilderTest {
    @Test
    fun buildsPayloadWithMaxImagesOptionsAndWebSearchForSeedream5() {
        val request = buildSeedreamRequest(
            baseInput(
                model = MODEL_SEEDREAM_5,
                references = listOf(
                    ReferenceImage(kind = ReferenceKind.Url, name = "u", value = "https://example.com/a.jpg")
                ),
                seed = "42",
                stream = "true",
                sequentialImageGeneration = "auto",
                maxImages = "3",
                outputFormat = "png",
                webSearch = "true"
            )
        )

        val json = request.toJsonObject()

        assertEquals(MODEL_SEEDREAM_5, json.getString("model"))
        assertEquals("测试 prompt", json.getString("prompt"))
        assertEquals("https://example.com/a.jpg", json.getJSONArray("image").getString(0))
        assertEquals(42L, json.getLong("seed"))
        assertTrue(json.getBoolean("stream"))
        assertEquals("auto", json.getString("sequential_image_generation"))
        assertEquals(3, json.getInt("max_images"))
        assertEquals(3, json.getJSONObject("sequential_image_generation_options").getInt("max_images"))
        assertEquals("png", json.getString("output_format"))
        assertEquals("web_search", json.getJSONArray("tools").getJSONObject(0).getString("type"))
    }

    @Test
    fun doesNotAttachWebSearchToolForSeedream45() {
        val request = buildSeedreamRequest(
            baseInput(model = MODEL_SEEDREAM_4_5, webSearch = "true")
        )

        val json = request.toJsonObject()
        assertFalse(json.has("tools"))
        assertFalse(json.has("output_format"))
    }

    @Test
    fun parsesBooleansLikeTheHtmlVersion() {
        assertEquals(true, normalizeBoolean("true"))
        assertEquals(false, normalizeBoolean("false"))
        assertEquals(null, normalizeBoolean(""))
    }

    @Test
    fun parsesDistinctUrlReferences() {
        val refs = parseUrlReferenceImages(
            """
            https://example.com/a.jpg
            not-a-url
            https://example.com/a.jpg
            http://example.com/b.png
            """.trimIndent()
        )

        assertEquals(2, refs.size)
        assertEquals("https://example.com/a.jpg", refs[0].value)
        assertEquals("http://example.com/b.png", refs[1].value)
    }

    private fun baseInput(
        model: String = MODEL_SEEDREAM_5,
        references: List<ReferenceImage> = emptyList(),
        seed: String = "",
        stream: String = "false",
        sequentialImageGeneration: String = "disabled",
        maxImages: String = "",
        outputFormat: String = "jpeg",
        webSearch: String = "false"
    ): RequestInput {
        return RequestInput(
            model = model,
            prompt = "测试 prompt",
            references = references,
            size = "2K",
            seed = seed,
            responseFormat = "url",
            watermark = "false",
            stream = stream,
            sequentialImageGeneration = sequentialImageGeneration,
            maxImages = maxImages,
            outputFormat = outputFormat,
            webSearch = webSearch
        )
    }
}
