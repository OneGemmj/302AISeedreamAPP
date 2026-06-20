package com.seedream.app.network

object RetryPolicy {
    const val maxRetries = 5

    fun delayMillis(attempt: Int): Long {
        if (attempt <= 0) return 0L
        return minOf(1_000L * (1L shl (attempt - 1)), 16_000L)
    }

    fun shouldRetryHttp(statusCode: Int): Boolean = statusCode >= 500
}
