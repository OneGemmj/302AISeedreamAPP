package com.seedream.app

import com.seedream.app.network.ImageExtractor
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageExtractorTest {
    @Test
    fun extractsDirectB64AndUrlImages() {
        val parsed = ImageExtractor.parseJson(
            """
            {
              "data": [
                { "url": "https://cdn.example.com/result.jpg" },
                { "b64_json": "abc123" }
              ]
            }
            """.trimIndent()
        )

        assertEquals(
            listOf("https://cdn.example.com/result.jpg"),
            ImageExtractor.directImagesFromResponse(parsed, "url")
        )
        assertEquals(
            listOf("https://cdn.example.com/result.jpg", "data:image/jpeg;base64,abc123"),
            ImageExtractor.directImagesFromResponse(parsed, "b64_json")
        )
    }

    @Test
    fun findsNestedLikelyImageUrlsWithoutDuplicates() {
        val parsed = ImageExtractor.parseJson(
            """
            {
              "nested": {
                "preview_image_url": "https://signed.example.com/seedream?id=1",
                "other": ["https://cdn.example.com/a.webp?x=1", "https://cdn.example.com/a.webp?x=1"]
              }
            }
            """.trimIndent()
        )

        assertEquals(
            setOf("https://signed.example.com/seedream?id=1", "https://cdn.example.com/a.webp?x=1"),
            ImageExtractor.findImageUrlsDeep(parsed).toSet()
        )
    }
}
