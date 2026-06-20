package com.seedream.app.network

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SeedreamApiClient(
    private val client: OkHttpClient = defaultClient()
) {
    suspend fun postJson(
        endpoint: String,
        apiKey: String,
        payloadJson: String,
        onCall: (Call) -> Unit = {}
    ): Response {
        val request = Request.Builder()
            .url(endpoint)
            .post(payloadJson.toRequestBody(JSON))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .build()
        val call = client.newCall(request)
        onCall(call)
        return call.await()
    }

    fun newLatencyClient(): OkHttpClient = client

    private suspend fun Call.await(): Response {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
            continuation.invokeOnCancellation { cancel() }
        }
    }

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaType()

        fun defaultClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }
    }
}
