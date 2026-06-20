package com.seedream.app

import com.seedream.app.network.RetryPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RetryPolicyTest {
    @Test
    fun usesExponentialBackoffCappedAtSixteenSeconds() {
        assertEquals(0L, RetryPolicy.delayMillis(0))
        assertEquals(1_000L, RetryPolicy.delayMillis(1))
        assertEquals(2_000L, RetryPolicy.delayMillis(2))
        assertEquals(16_000L, RetryPolicy.delayMillis(5))
        assertEquals(16_000L, RetryPolicy.delayMillis(8))
    }

    @Test
    fun retriesServerErrorsOnly() {
        assertFalse(RetryPolicy.shouldRetryHttp(400))
        assertFalse(RetryPolicy.shouldRetryHttp(429))
        assertTrue(RetryPolicy.shouldRetryHttp(500))
        assertTrue(RetryPolicy.shouldRetryHttp(503))
    }
}
