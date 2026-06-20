package com.seedream.app.storage

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_images",
    indices = [Index(value = ["timestamp"])]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: String,
    val localPath: String?,
    val prompt: String,
    val model: String,
    val timestamp: Long
)
