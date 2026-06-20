package com.seedream.app.service

import com.seedream.app.model.ResultImage
import com.seedream.app.model.StatusKind
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface GenerationEvent {
    data object Started : GenerationEvent
    data object Completed : GenerationEvent
    data object Cancelled : GenerationEvent
    data class Status(val message: String, val kind: StatusKind = StatusKind.Normal) : GenerationEvent
    data class RawResponse(val text: String) : GenerationEvent
    data class Result(val image: ResultImage) : GenerationEvent
    data class RetryScheduled(val attempt: Int, val maxRetries: Int, val delayMillis: Long) : GenerationEvent
    data class Failed(val message: String) : GenerationEvent
}

object GenerationEvents {
    private val _events = MutableSharedFlow<GenerationEvent>(extraBufferCapacity = 128)
    val events = _events.asSharedFlow()

    suspend fun emit(event: GenerationEvent) {
        _events.emit(event)
    }

    fun tryEmit(event: GenerationEvent) {
        _events.tryEmit(event)
    }
}
