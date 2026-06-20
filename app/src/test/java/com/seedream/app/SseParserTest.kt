package com.seedream.app

import com.seedream.app.network.SseParser
import org.junit.Assert.assertEquals
import org.junit.Test

class SseParserTest {
    @Test
    fun parsesDataLinesAcrossChunksAndIgnoresDone() {
        val parser = SseParser()

        assertEquals(emptyList<Any>(), parser.accept("data: {\"data\":["))
        val events = parser.accept("{\"url\":\"https://example.com/a.jpg\"}]}\n\ndata: [DONE]\n")

        assertEquals(1, events.size)
        assertEquals("https://example.com/a.jpg", events[0].getJSONArray("data").getJSONObject(0).getString("url"))
    }
}
