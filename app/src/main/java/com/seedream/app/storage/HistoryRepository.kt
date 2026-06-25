package com.seedream.app.storage

import android.content.Context
import android.net.Uri
import java.io.File

data class HistoryPage(
    val items: List<HistoryEntity>,
    val totalCount: Int
)

class HistoryRepository(context: Context) {
    private val appContext = context.applicationContext
    private val dao = AppDatabase.get(appContext).historyDao()
    private val imageStorage = ImageStorage(appContext)

    suspend fun load(limit: Int, keyword: String): HistoryPage {
        val cleanKeyword = keyword.trim()
        return HistoryPage(
            items = dao.page(limit.coerceAtLeast(1), cleanKeyword),
            totalCount = dao.countMatching(cleanKeyword)
        )
    }

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
