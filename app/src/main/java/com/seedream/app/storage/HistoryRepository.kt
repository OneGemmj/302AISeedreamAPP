package com.seedream.app.storage

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

class HistoryRepository(context: Context) {
    private val appContext = context.applicationContext
    private val dao = AppDatabase.get(appContext).historyDao()
    private val imageStorage = ImageStorage(appContext)

    val history: Flow<List<HistoryEntity>> = dao.observeAll()

    suspend fun saveGeneratedImage(src: String, prompt: String, model: String): String {
        return runCatching {
            val cachedPath = imageStorage.cacheImage(src)
            val storedSource = if (src.startsWith("data:image", ignoreCase = true) && cachedPath != null) "" else src
            dao.insert(
                HistoryEntity(
                    source = storedSource,
                    localPath = cachedPath,
                    prompt = prompt,
                    model = model,
                    timestamp = System.currentTimeMillis()
                )
            )
            cachedPath?.let { Uri.fromFile(File(it)).toString() } ?: src
        }.getOrElse {
            if (src.startsWith("data:image", ignoreCase = true)) "" else src
        }
    }

    suspend fun delete(id: Long) {
        val item = dao.byId(id)
        dao.deleteById(id)
        imageStorage.deleteCachedFile(item?.localPath)
    }

    suspend fun clearAll() {
        dao.allOnce().forEach { imageStorage.deleteCachedFile(it.localPath) }
        dao.clear()
    }

    suspend fun saveToPictures(src: String): Uri? {
        return imageStorage.saveToPictures(src)
    }

    fun displaySource(item: HistoryEntity): String {
        return item.localPath?.let { Uri.fromFile(File(it)).toString() } ?: item.source
    }
}
